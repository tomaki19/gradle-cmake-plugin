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
import org.gradle.api.NamedDomainObjectContainer;
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
import ch.tomaki.gradle.cmake.tasks.CMakeTaskRegistry;
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

        final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project);
        final CMakeResolvedBuild resolvedBuild = new CMakeResolvedBuild(project.getName());
        final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName();
        taskRegistry.register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project),
            resolvedBuild);
        final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
        taskRegistry.register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(project),
            resolvedBuild).configure((task) -> task.dependsOn(assembleConfigTaskName));
        taskRegistry.getGradleAssembleTask().configure((task) -> task.dependsOn(assembleListsTaskName));

        project.getLogger().debug("%s: Evaluating %d Toolchains..."
            .formatted(project.getName(), extension.getToolchains().size()));
        for (final CMakeToolchain toolchain : extension.getToolchains()) {
          if (Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().getOrNull())) {
            final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
            configureTasks(taskRegistry, resolvedToolchain);
            resolvedBuild.put(resolvedToolchain.getName(), resolvedToolchain);
          }
        }

        project.getLogger().debug("%s: Evaluating %d Custom Tasks..."
            .formatted(project.getName(), extension.getCustomTasks().size()));
        for (final Map.Entry<String, String[]> customTask : extension.getCustomTasks().entrySet()) {
          for (final String toolchainName : customTask.getValue()) {
            resolvedBuild.forToolchain(toolchainName, (toolchain) -> {
              configureTask(taskRegistry, customTask.getKey(), toolchain);
            });
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
        forBinaries(extension.getLibraries(), resolvedBuild,
            (CMakeLibrary library, CMakeResolvedToolchain resolvedToolchain, String buildConfig) -> {
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
                  configureTasks(taskRegistry, resolvedLibrary, buildTarget, projectModuleDependencies);
                  resolvedBuild.addAll(projectModuleDependencies);
                }

                if (resolvedLibrary.isBuildShared()) {
                  final String buildTarget = CMakeListsConventions.sharedLibraryTarget(resolvedLibrary.getName(),
                      resolvedToolchain, buildConfig);
                  configureTasks(taskRegistry, resolvedLibrary, buildTarget, projectModuleDependencies);
                  resolvedBuild.addAll(projectModuleDependencies);
                }
              }
              resolvedBuild.add(resolvedLibrary);
            });

        project.getLogger().debug("%s: Evaluating %d Applications..."
            .formatted(project.getName(), extension.getApplications().size()));
        forBinaries(extension.getApplications(), resolvedBuild,
            (CMakeBinary application, CMakeResolvedToolchain resolvedToolchain, String buildConfig) -> {
              final CMakeResolvedApplication resolvedApplication = new CMakeResolvedApplication(application,
                  extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
              resolvedApplication.addLibraryDependencies(resolvedToolchain.getPrivateApplicationLinkDependencies(),
                  extension.getFindPackages().getAsMap(), project);
              final String buildTarget = CMakeListsConventions.applicationTarget(resolvedApplication.getName(),
                  resolvedToolchain, buildConfig);
              configureTasks(taskRegistry, resolvedApplication, buildTarget);
              resolvedBuild.addAll(resolvedApplication.getPrivateProjectModuleDependencies());
              resolvedBuild.add(resolvedApplication);
            });

        project.getLogger().debug("%s: Evaluating %d Tests..."
            .formatted(project.getName(), extension.getTests().size()));
        forBinaries(extension.getTests(), resolvedBuild,
            (CMakeTest test, CMakeResolvedToolchain resolvedToolchain, String buildConfig) -> {
              final CMakeResolvedTest resolvedTest = new CMakeResolvedTest(test, extension.getFindPackages().getAsMap(),
                  resolvedToolchain, buildConfig, project);
              resolvedTest.addLibraryDependencies(resolvedToolchain.getPrivateTestLinkDependencies(),
                  extension.getFindPackages().getAsMap(), project);
              final String buildTarget = CMakeListsConventions.testTarget(resolvedTest.getName(), resolvedToolchain,
                  buildConfig);
              configureTasks(taskRegistry, resolvedTest, buildTarget);
              resolvedBuild.addAll(resolvedTest.getPrivateProjectModuleDependencies());
              resolvedBuild.add(resolvedTest);
            });
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e.getCause());
    }
  }

  private interface ValidBuildConfigConsumer<T extends CMakeBinary> {
    void accept(final T cmakeBinary, final CMakeResolvedToolchain resolvedToolchain, final String buildConfigName);
  }

  private static <T extends CMakeBinary> void forBinaries(final NamedDomainObjectContainer<T> cmakeBinaries,
      final CMakeResolvedBuild resolvedBuild, final ValidBuildConfigConsumer<T> consumer) {
    for (final T cmakeBinary : cmakeBinaries) {
      for (final String toolchainName : cmakeBinary.getBuildToolchains().get()) {
        resolvedBuild.forToolchain(toolchainName, (toolchain) -> {
          for (final String buildConfig : toolchain.getBuildConfigs()) {
            consumer.accept(cmakeBinary, toolchain, buildConfig);
          }
        });
      }
    }
  }

  private void configureTask(final CMakeTaskRegistry taskRegistry, final String name,
      final CMakeResolvedToolchain toolchain) {
    final String cmakeCustomTaskName = CMakeTasksConventions.customTaskName(name, toolchain.getName());
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    taskRegistry.register(cmakeCustomTaskName, CMakeExec.class, toolchain)
        .configure((task) -> task.dependsOn(cmakeConfigureTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedToolchain resolvedToolchain) {

    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(resolvedToolchain.getName());
    taskRegistry.register(cmakeConfigureTaskName, CMakeConfigureExec.class, resolvedToolchain)
        .configure((task) -> task.dependsOn(CMakeTasksConventions.assembleListsTaskName()));
    taskRegistry.getGradleAssembleTask().configure((task) -> task.dependsOn(cmakeConfigureTaskName));

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildTaskName(resolvedToolchain.getName());
    taskRegistry.register(cmakeToolchainBuildAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_BUILD));

    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkTaskName(resolvedToolchain.getName());
    taskRegistry.register(cmakeToolchainCheckAllTaskName)
        .configure((task) -> task.setGroup(CMakeTasksConventions.GROUP_CHECK));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedLibrary resolvedLibrary,
      final String buildTarget, final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedLibrary.getToolchain().getName());
    taskRegistry.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName, projectModuleDependencies);

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedLibrary).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistry.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
        projectModuleDependencies);

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(resolvedLibrary.getToolchain().getName());
    taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (resolvedLibrary.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistry.register(packageTaskName, CMakePackage.class, buildTarget, resolvedLibrary.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry,
      final CMakeResolvedApplication resolvedApplication, final String buildTarget) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedApplication.getToolchain().getName());
    taskRegistry.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedApplication)
        .configure((task) -> {
          task.dependsOn(cmakeConfigureTaskName);
        });
    taskRegistry.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
        resolvedApplication.getPrivateProjectModuleDependencies());

    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions
        .buildTaskName(resolvedApplication.getToolchain().getName());
    taskRegistry.configure(cmakeToolchainBuildAllTaskName, (task) -> task.dependsOn(cmakeBuildTaskName));

    if (resolvedApplication.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      taskRegistry.register(packageTaskName, CMakePackage.class, buildTarget, resolvedApplication.getToolchain())
          .configure((task) -> task.dependsOn(cmakeBuildTaskName));
    }
    taskRegistry.getGradleBuildTask().configure((task) -> task.dependsOn(cmakeBuildTaskName));
  }

  private void configureTasks(final CMakeTaskRegistry taskRegistry, final CMakeResolvedTest resolvedTest,
      final String buildTarget) {

    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedTest.getToolchain().getName());
    taskRegistry.configureTaskProjectModuleConfigureDependencies(cmakeConfigureTaskName,
        resolvedTest.getPrivateProjectModuleDependencies());

    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    taskRegistry.register(cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedTest).configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    taskRegistry.configureTaskProjectModuleBuildDependencies(cmakeBuildTaskName,
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
