/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.CMakeTest;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeConfigFile;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.files.CMakeListsFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedFindPackage;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import ch.tomaki.gradle.cmake.tasks.CMakeAssemble;
import ch.tomaki.gradle.cmake.tasks.CMakeBuildExec;
import ch.tomaki.gradle.cmake.tasks.CMakeConfigureExec;
import ch.tomaki.gradle.cmake.tasks.CMakeExec;
import ch.tomaki.gradle.cmake.tasks.CMakePackage;
import ch.tomaki.gradle.cmake.tasks.CMakeTaskRegistery;
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTestExec;

public class CMakePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.allprojects(this::allProjects);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void allProjects(final Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);
  }

  private void afterEvaluate(final Project project) {
    project.getLogger().debug("%s: Evaluating Project...".formatted(project.getName()));
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      if (Objects.nonNull(extension)) {

        final CMakeTaskRegistery taskRegistery = new CMakeTaskRegistery(project);
        final CMakeResolvedBuild resolvedBuild = new CMakeResolvedBuild(project.getName());
        final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName();
        taskRegistery.register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project),
            resolvedBuild);
        final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
        taskRegistery.register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(project),
            resolvedBuild).configure((task) -> task.dependsOn(assembleConfigTaskName));
        taskRegistery.getGradleAssembleTask().configure((task) -> task.dependsOn(assembleListsTaskName));

        project.getLogger().debug("%s: Evaluating %d Toolchains..."
            .formatted(project.getName(), extension.getToolchains().size()));
        for (final CMakeToolchain toolchain : extension.getToolchains()) {
          if (Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().getOrNull())) {
            final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
            configureTasks(taskRegistery, resolvedToolchain);
            resolvedBuild.put(resolvedToolchain.getName(), resolvedToolchain);
          }
        }

        project.getLogger().debug("%s: Evaluating %d Custom Tasks..."
            .formatted(project.getName(), extension.getCustomTasks().size()));
        for (final Map.Entry<String, String[]> customTask : extension.getCustomTasks().entrySet()) {
          for (final String toolchainName : customTask.getValue()) {
            if (resolvedBuild.getToolchains().containsKey(toolchainName)) {
              final CMakeResolvedToolchain resolvedToolchain = resolvedBuild.getToolchains().get(toolchainName);
              configureTask(taskRegistery, customTask.getKey(), resolvedToolchain);
            }
          }
        }

        project.getLogger().debug("%s: Evaluating %d FindPackages..."
            .formatted(project.getName(), extension.getFindPackages().size()));
        for (final CMakeFindPackage findPackage : extension.getFindPackages()) {
          final CMakeResolvedFindPackage resolvedFindPackage = new CMakeResolvedFindPackage(findPackage);
          resolvedBuild.add(resolvedFindPackage);
        }

        project.getLogger().debug("%s: Evaluating %d Libraries..."
            .formatted(project.getName(), extension.getLibraries().size()));
        for (final CMakeLibrary library : extension.getLibraries()) {
          for (final String toolchainName : library.getBuildToolchains().get()) {
            if (resolvedBuild.getToolchains().containsKey(toolchainName)) {
              final CMakeResolvedToolchain resolvedToolchain = resolvedBuild.getToolchains().get(toolchainName);
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(
                    library, extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
                resolvedLibrary.addLibraryDependencies(resolvedToolchain.getPrivateLibraryLinkDependencies(),
                    extension.getFindPackages().getAsMap(), project);
                final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new ArrayList<>();
                projectModuleDependencies.addAll(resolvedLibrary.getPrivateProjectModuleDependencies());
                projectModuleDependencies.addAll(resolvedLibrary.getPublicProjectModuleDependencies());
                if (!resolvedLibrary.getSources().isEmpty()) {

                  if (resolvedLibrary.isBuildStatic()) {
                    final String buildTarget = CMakeListsConventions.staticLibraryTarget(resolvedLibrary.getName(),
                        resolvedToolchain, buildConfig);
                    configureTasks(taskRegistery, resolvedLibrary, buildTarget, projectModuleDependencies);
                    resolvedBuild.addAll(projectModuleDependencies);
                  }

                  if (resolvedLibrary.isBuildShared()) {
                    final String buildTarget = CMakeListsConventions.sharedLibraryTarget(resolvedLibrary.getName(),
                        resolvedToolchain, buildConfig);
                    configureTasks(taskRegistery, resolvedLibrary, buildTarget, projectModuleDependencies);
                    resolvedBuild.addAll(projectModuleDependencies);
                  }
                }
                resolvedBuild.add(resolvedLibrary);
              }
            }
          }
        }

        project.getLogger().debug("%s: Evaluating %d Applications..."
            .formatted(project.getName(), extension.getApplications().size()));
        for (final CMakeBinary application : extension.getApplications()) {
          for (final String toolchainName : application.getBuildToolchains().get()) {
            if (resolvedBuild.getToolchains().containsKey(toolchainName)) {
              final CMakeResolvedToolchain resolvedToolchain = resolvedBuild.getToolchains().get(toolchainName);
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedApplication resolvedApplication = new CMakeResolvedApplication(application,
                    extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
                resolvedApplication.addLibraryDependencies(resolvedToolchain.getPrivateApplicationLinkDependencies(),
                    extension.getFindPackages().getAsMap(), project);
                final String buildTarget = CMakeListsConventions.applicationTarget(resolvedApplication.getName(),
                    resolvedToolchain, buildConfig);
                configureTasks(taskRegistery, resolvedApplication, buildTarget, project.getName());
                resolvedBuild.addAll(resolvedApplication.getPrivateProjectModuleDependencies());
                resolvedBuild.add(resolvedApplication);
              }
            }
          }
        }

        project.getLogger().debug("%s: Evaluating %d Tests..."
            .formatted(project.getName(), extension.getTests().size()));
        for (final CMakeTest test : extension.getTests()) {
          for (final String toolchainName : test.getBuildToolchains().get()) {
            if (resolvedBuild.getToolchains().containsKey(toolchainName)) {
              final CMakeResolvedToolchain resolvedToolchain = resolvedBuild.getToolchains().get(toolchainName);
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedTest resolvedTest = new CMakeResolvedTest(test,
                    extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
                resolvedTest.addLibraryDependencies(resolvedToolchain.getPrivateTestLinkDependencies(),
                    extension.getFindPackages().getAsMap(), project);
                final String buildTarget = CMakeListsConventions.testTarget(resolvedTest.getName(), resolvedToolchain,
                    buildConfig);
                configureTasks(taskRegistery, resolvedTest, buildTarget, project.getName());
                resolvedBuild.addAll(resolvedTest.getPrivateProjectModuleDependencies());
                resolvedBuild.add(resolvedTest);
              }
            }
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private void configureTask(final CMakeTaskRegistery taskRegistery, final String name,
      final CMakeResolvedToolchain toolchain) {
    final String cmakeCustomTaskName = CMakeTasksConventions.customTaskName(name, toolchain.getName());
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistery.register(cmakeCustomTaskName, CMakeExec.class, toolchain)
        .configure((task) -> task.dependsOn(cmakeConfigureTaskName));
  }

  private void configureTasks(final CMakeTaskRegistery taskRegistery, final CMakeResolvedToolchain resolvedToolchain) {
    
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(resolvedToolchain.getName());
    taskRegistery.register(cmakeConfigureTaskName, CMakeConfigureExec.class, resolvedToolchain)
        .configure((task) -> task.dependsOn(CMakeTasksConventions.assembleListsTaskName()));
    taskRegistery.getGradleAssembleTask().configure((task) -> task.dependsOn(cmakeConfigureTaskName));

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildTaskName(resolvedToolchain.getName());
    taskRegistery.register(cmakeToolchainBuildAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_BUILD));

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkTaskName(resolvedToolchain.getName());
    taskRegistery.register(cmakeToolchainCheckAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_CHECK));
  }

  private void configureTasks(final CMakeTaskRegistery taskRegistery, final CMakeResolvedLibrary resolvedLibrary,
      final String buildTarget, final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedLibrary.getToolchain().getName());
    taskRegistery.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName, projectModuleDependencies);

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistery.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedLibrary).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistery.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
        projectModuleDependencies);

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(resolvedLibrary.getToolchain().getName());
    taskRegistery.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (resolvedLibrary.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistery.register(packageTaskName, CMakePackage.class, buildTarget, resolvedLibrary.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistery.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistery taskRegistery,
      final CMakeResolvedApplication resolvedApplication, final String buildTarget, final String projectName) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedApplication.getToolchain().getName());
    taskRegistery.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistery.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedApplication)
        .configure((task) -> {
          task.dependsOn(cmakeConfigureTaskName);
        });
    taskRegistery.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(resolvedApplication.getToolchain().getName());
    taskRegistery.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (resolvedApplication.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistery.register(packageTaskName, CMakePackage.class, buildTarget, resolvedApplication.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistery.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistery taskRegistery, final CMakeResolvedTest resolvedTest,
      final String buildTarget, final String projectName) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedTest.getToolchain().getName());
    taskRegistery.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName,
        resolvedTest.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistery.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedTest).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistery.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
        resolvedTest.getPrivateProjectModuleDependencies());

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions
        .checkTaskName(resolvedTest.getToolchain().getName());
    taskRegistery.configure(cmakeToolchainCheckAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(buildTarget);
    taskRegistery.register(cmakeTestTaskName, CMakeTestExec.class, buildTarget, resolvedTest)
        .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    taskRegistery.getGradleCheckTask().configure((task) -> task.dependsOn(cmakeTestTaskName));
  }

}
