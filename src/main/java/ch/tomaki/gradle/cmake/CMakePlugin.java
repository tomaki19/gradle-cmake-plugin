/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeValidator;
import ch.tomaki.gradle.cmake.files.CMakeConfigFile;
import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.files.CMakeListsFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolver;
import ch.tomaki.gradle.cmake.tasks.CMakeAssemble;
import ch.tomaki.gradle.cmake.tasks.CMakeBuildExec;
import ch.tomaki.gradle.cmake.tasks.CMakeConfigureExec;
import ch.tomaki.gradle.cmake.tasks.CMakeExec;
import ch.tomaki.gradle.cmake.tasks.CMakePackage;
import ch.tomaki.gradle.cmake.tasks.CMakeTaskRegistry;
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTestExec;

public class CMakePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.allprojects(this::allProjects);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void allProjects(final Project project) {
    try {
      project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void afterEvaluate(final Project project) {
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      if (Objects.nonNull(extension)) {

        /* Validate */

        CMakeValidator.validateToolchains(extension.getToolchains());
        CMakeValidator.validateLibraries(extension.getLibraries());
        CMakeValidator.validateApplications(extension.getApplications());
        CMakeValidator.validateTests(extension.getTests());

        /* Resolve */

        final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
        final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
            extension.getApplications(), extension.getTests());

        /* Tasks */

        final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project);

        extension.getCustomTasks().forEach((taskName, toolchainNames) -> {
          toolchains.forEach((resolvedToolchain) -> {
            if (toolchainNames.contains(resolvedToolchain.getName())) {
              configureCustomTask(taskRegistry, taskName, resolvedToolchain);
            }
          });
        });

        configureAssembleListsTask(taskRegistry, toolchains, project);
        for (final CMakeResolvedToolchain toolchain : toolchains) {
          configureAssembleConfigTask(taskRegistry, toolchain, project);

          for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
            if (!library.getSources().isEmpty()) {
              if (library.isBuildStatic()) {
                configureTasks(taskRegistry, library, toolchain, CMakeLinkType.STATIC);
              }
              if (library.isBuildShared()) {
                configureTasks(taskRegistry, library, toolchain, CMakeLinkType.SHARED);
              }
            }
          }
          for (final CMakeResolvedApplication application : toolchain.getApplications()) {
            configureTasks(taskRegistry, application, toolchain);
          }
          for (final CMakeResolvedTest test : toolchain.getTests()) {
            configureTasks(taskRegistry, test, toolchain);
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void configureCustomTask(final CMakeTaskRegistry taskRegistry, final String name,
      final CMakeResolvedToolchain toolchain) {
    final String cmakeCustomTaskName = CMakeTasksConventions.customTaskName(name, toolchain.getName());
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistry.register(cmakeCustomTaskName, CMakeExec.class, toolchain.getName(), toolchain.getEnvironmentFile())
        .configure((task) -> task.dependsOn(cmakeConfigureTaskName));
  }

  private void configureAssembleListsTask(final CMakeTaskRegistry taskRegistry,
      final Collection<CMakeResolvedToolchain> toolchains, final Project project) throws FileNotFoundException {
    final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
    taskRegistry.register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project));
    taskRegistry.getGradleAssembleTask().configure((task) -> task.dependsOn(assembleListsTaskName));
  }

  private void configureAssembleConfigTask(final CMakeTaskRegistry taskRegistry, final CMakeResolvedToolchain toolchain,
      final Project project) throws FileNotFoundException {
    final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName(toolchain.getName());
    taskRegistry.register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project, toolchain));

    toolchain.getProjectPackages().stream()
        .filter(dependency -> !Objects.equals(project, dependency.getProject()))
        .forEach(dependency -> {
          project.evaluationDependsOn(dependency.getProject().getPath());
        });

    taskRegistry.getGradleAssembleTask().configure((task) -> toolchain.getProjectPackages().stream()
        .filter(dependency -> !Objects.equals(project, dependency.getProject()))
        .forEach(dependency -> {
          task.dependsOn(CMakeTasksConventions.assembleListsTaskName());
          task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(toolchain.getName()));
        }));

    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistry.register(cmakeConfigureTaskName, CMakeConfigureExec.class, toolchain)
        .configure((task) -> {
          task.dependsOn(CMakeTasksConventions.assembleListsTaskName());
          toolchain.getProjectPackages().stream()
              .filter(dependency -> !Objects.equals(project, dependency.getProject()))
              .forEach(dependency -> {
                task.mustRunAfter(CMakeTasksConventions.configureTaskName(dependency.getProject(),
                    toolchain.getName()));
                task.dependsOn(CMakeTasksConventions.assembleListsTaskName(dependency.getProject()));
                task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(dependency.getProject(),
                    toolchain.getName()));
              });
        });

    if (!toolchain.getLibraries().isEmpty() || !toolchain.getApplications().isEmpty()
        || !toolchain.getTests().isEmpty()) {
      final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
      taskRegistry.register(cmakeToolchainBuildAllTaskName)
          .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_BUILD));
    }

    if (!toolchain.getTests().isEmpty()) {
      final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
      taskRegistry.register(cmakeToolchainCheckAllTaskName)
          .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_CHECK));
    }
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType linkType) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      final String buildTarget = CMakeFileConventions.libraryTarget(library.getName(), toolchain, linkType,
          buildConfig);
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(library.getName(),
          toolchain.getName(), linkType, buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, toolchain, buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      final Collection<CMakeResolvedProjectPackageDependency> projectModuleDependencies = new ArrayList<>();
      projectModuleDependencies.addAll(library.getPrivateProjectPackageDependencies());
      projectModuleDependencies.addAll(library.getPublicProjectPackageDependencies());
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName, toolchain,
          projectModuleDependencies);
      taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      if (library.isPackageBuildOutputs()) {
        final String packageTaskName = CMakeTasksConventions.packageTaskName(library.getName(),
            toolchain.getName(), linkType, buildConfig);
        taskRegistry.register(packageTaskName, CMakePackage.class,
            buildTarget, toolchain).configure((task) -> {
              task.dependsOn(cmakeBuildTaskName);
            });
      }
      taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedApplication application,
      final CMakeResolvedToolchain toolchain) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      final String buildTarget = CMakeFileConventions.applicationTarget(application.getName(), toolchain, buildConfig);
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(application.getName(),
          toolchain.getName(), buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, toolchain, buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName, toolchain,
          application.getPrivateProjectPackageDependencies());
      taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      if (application.isPackageBuildOutputs()) {
        final String packageTaskName = CMakeTasksConventions.packageTaskName(application.getName(),
            toolchain.getName(), buildConfig);
        taskRegistry.register(packageTaskName, CMakePackage.class,
            buildTarget, toolchain).configure((task) -> {
              task.dependsOn(cmakeBuildTaskName);
            });
      }
      taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedTest test,
      final CMakeResolvedToolchain toolchain) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      final String buildTarget = CMakeFileConventions.testTarget(test.getName(), toolchain, buildConfig);
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(test.getName(),
          toolchain.getName(), buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, toolchain, buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName, toolchain,
          test.getPrivateProjectPackageDependencies());
      taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
      final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(test.getName(),
          toolchain.getName(), buildConfig);
      taskRegistry.register(cmakeTestTaskName, CMakeTestExec.class,
          buildTarget, toolchain, buildConfig).configure((task) -> {
            task.dependsOn(cmakeBuildTaskName);
          });
      taskRegistry.getGradleCheckTask().configure((task) -> task.dependsOn(cmakeToolchainCheckAllTaskName,
          cmakeTestTaskName));
    }
  }

}
