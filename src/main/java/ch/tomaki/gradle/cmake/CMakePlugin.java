/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
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
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;
import ch.tomaki.gradle.cmake.extension.CMakeValidator;
import ch.tomaki.gradle.cmake.files.CMakeConfigFile;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.files.CMakeListsFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;
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
            final String buildTarget = CMakeListsConventions.libraryTarget(library.getName(),
                library.getToolchain(), CMakeLinkType.STATIC, library.getBuildConfig());
            configureTasks(taskRegistry, library, buildTarget);
          }
          if (library.isBuildShared()) {
            final String buildTarget = CMakeListsConventions.libraryTarget(library.getName(),
                library.getToolchain(), CMakeLinkType.SHARED, library.getBuildConfig());
            configureTasks(taskRegistry, library, buildTarget);
          }
        });

        resolvedBuild.getResolvedApplications().forEach((application) -> {
          final String buildTarget = CMakeListsConventions.applicationTarget(application.getName(),
              application.getToolchain(), application.getBuildConfig());
          configureTasks(taskRegistry, application, buildTarget);
        });

        resolvedBuild.getResolvedTests().forEach((test) -> {
          final String buildTarget = CMakeListsConventions.testTarget(test.getName(), test.getToolchain(),
              test.getBuildConfig());
          configureTasks(taskRegistry, test, buildTarget);
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

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildTaskName(toolchain.getName());
    taskRegistry.register(cmakeToolchainBuildAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_BUILD));

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkTaskName(toolchain.getName());
    taskRegistry.register(cmakeToolchainCheckAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_CHECK));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedLibrary library,
      final String buildTarget) {
    final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new ArrayList<>();
    projectModuleDependencies.addAll(library.getPrivateProjectModuleDependencies());
    projectModuleDependencies.addAll(library.getPublicProjectModuleDependencies());

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(library.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName, projectModuleDependencies);

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, library).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName, projectModuleDependencies);

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(library.getToolchain().getName());
    taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (library.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistry.register(packageTaskName, CMakePackage.class, buildTarget, library.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedApplication resolvedApplication,
      final String buildTarget) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedApplication.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedApplication)
        .configure((task) -> {
          task.dependsOn(cmakeConfigureTaskName);
        });
    taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(resolvedApplication.getToolchain().getName());
    taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (resolvedApplication.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistry
          .register(packageTaskName, CMakePackage.class, buildTarget, resolvedApplication.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedTest resolvedTest,
      final String buildTarget) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedTest.getToolchain().getName());
    taskRegistry.configureConfigureTaskProjectModuleDependencies(cmakeConfigureTaskName,
        resolvedTest.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedTest).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistry.configureBuildTaskProjectModuleDependencies(cmakeBuildTaskName,
        resolvedTest.getPrivateProjectModuleDependencies());

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions
        .checkTaskName(resolvedTest.getToolchain().getName());
    taskRegistry.configure(cmakeToolchainCheckAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(buildTarget);
    taskRegistry.register(cmakeTestTaskName, CMakeTestExec.class, buildTarget, resolvedTest)
        .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    taskRegistry.getGradleCheckTask().configure((task) -> task.dependsOn(cmakeTestTaskName));
  }

}
