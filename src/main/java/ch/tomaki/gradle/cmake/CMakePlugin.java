/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeValidator;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeConfigFile;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.files.CMakeListsFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinaryLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
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
        CMakeValidator.validateToolchains(extension.getToolchains());
        CMakeValidator.validateLibraries(extension.getLibraries());
        CMakeValidator.validateApplications(extension.getApplications());
        CMakeValidator.validateTests(extension.getTests());

        final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
            extension.getToolchains());
        final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
            extension.getApplications(), extension.getTests());

        final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project);

        final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName();
        taskRegistry.register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project), resolvedBuild);
        final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
        taskRegistry.register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(project), resolvedBuild)
            .configure((task) -> task.dependsOn(assembleConfigTaskName));
        taskRegistry.configureAssembleConfigTaskProjectModuleDependencies(assembleListsTaskName,
            resolvedBuild.getResolvedProjectModules());
        taskRegistry.getGradleAssembleTask().configure((task) -> task.dependsOn(assembleListsTaskName));

        cmakeResolver.forToolchains((toolchain) -> configureTasks(taskRegistry, toolchain));

        resolvedBuild.getResolvedLibraries().forEach((library) -> {
          if (library.isBuildStatic()) {
            configureTasks(taskRegistry, library, CMakeLinkType.STATIC);
          }
          if (library.isBuildShared()) {
            configureTasks(taskRegistry, library, CMakeLinkType.SHARED);
          }
        });

        resolvedBuild.getResolvedApplications().forEach((application) -> {
          configureTasks(taskRegistry, application);
        });

        resolvedBuild.getResolvedTests().forEach((test) -> {
          configureTasks(taskRegistry, test);
        });

        extension.getCustomTasks().forEach((taskName, toolchainNames) -> {
          toolchainNames.stream().forEach((toolchainName) -> {
            cmakeResolver.forToolchain(toolchainName, (toolchain) -> {
              configureTask(taskRegistry, taskName, toolchain);
            });
          });
        });
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void configureTask(final CMakeTaskRegistry taskRegistry, final String name,
      final CMakeToolchain toolchain) {
    final String cmakeCustomTaskName = CMakeTasksConventions.customTaskName(name, toolchain.getName());
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistry.register(cmakeCustomTaskName, CMakeExec.class,
        toolchain.getName(), Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull()))
        .configure((task) -> task.dependsOn(cmakeConfigureTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeToolchain toolchain) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistry.register(cmakeConfigureTaskName, CMakeConfigureExec.class, toolchain)
        .configure((task) -> task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(),
            CMakeTasksConventions.assembleListsTaskName()));

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    taskRegistry.register(cmakeToolchainBuildAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_BUILD));

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
    taskRegistry.register(cmakeToolchainCheckAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_CHECK));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedBinaryLibrary library,
      final CMakeLinkType linkType) {
    final String buildTarget = CMakeListsConventions.libraryBinaryTarget(library.getName(), linkType);

    final List<CMakeResolvedProjectDependency> projectModuleDependencies = new ArrayList<>();
    projectModuleDependencies.addAll(library.getPrivateProjectDependencies());
    projectModuleDependencies.addAll(library.getPublicProjectDependencies());

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(library.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName, projectModuleDependencies);

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildAllTaskName(library.getToolchain().getName());
    for (final String buildConfig : library.getToolchain().getBuildConfigs()) {
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(library.getName(),
          library.getToolchain().getName(), linkType, buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, library.getToolchain(), buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName, projectModuleDependencies);
      taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      if (library.isPackageBuildOutputs()) {
        final String packageTaskName = CMakeTasksConventions.packageTaskName(library.getName(),
            library.getToolchain().getName(), linkType, buildConfig);
        taskRegistry.register(packageTaskName, CMakePackage.class,
            buildTarget, library.getToolchain()).configure((task) -> {
              task.dependsOn(cmakeBuildTaskName);
            });
      }
      taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedApplication application) {
    final String buildTarget = CMakeListsConventions.applicationTarget(application.getName());

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(application.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName,
        application.getPrivateProjectDependencies());

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildAllTaskName(application.getToolchain().getName());
    for (final String buildConfig : application.getToolchain().getBuildConfigs()) {
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(application.getName(),
          application.getToolchain().getName(), buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, application.getToolchain(), buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName,
          application.getPrivateProjectDependencies());
      taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      if (application.isPackageBuildOutputs()) {
        final String packageTaskName = CMakeTasksConventions.packageTaskName(application.getName(),
            application.getToolchain().getName(), buildConfig);
        taskRegistry.register(packageTaskName, CMakePackage.class,
            buildTarget, application.getToolchain()).configure((task) -> {
              task.dependsOn(cmakeBuildTaskName);
            });
      }
      taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedTest test) {
    final String buildTarget = CMakeListsConventions.testTarget(test.getName());

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(test.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName,
        test.getPrivateProjectDependencies());

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkAllTaskName(test.getToolchain().getName());
    for (final String buildConfig : test.getToolchain().getBuildConfigs()) {
      final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(test.getName(),
          test.getToolchain().getName(), buildConfig);
      taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class,
          buildTarget, test.getToolchain(), buildConfig).configure((task) -> {
            task.dependsOn(cmakeConfigureTaskName);
          });
      taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName,
          test.getPrivateProjectDependencies());
      taskRegistry.configure(cmakeToolchainCheckAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

      final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(test.getName(),
          test.getToolchain().getName(), buildConfig);
      taskRegistry.register(cmakeTestTaskName, CMakeTestExec.class,
          buildTarget, test.getToolchain(), buildConfig).configure((task) -> {
            task.dependsOn(cmakeBuildTaskName);
          });
      taskRegistry.getGradleCheckTask().configure((task) -> task.dependsOn(cmakeTestTaskName));
    }
  }

}
