/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import java.util.*;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
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
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTestExec;

public class CMakePlugin implements Plugin<Project> {

  private final Map<String, TaskProvider<Task>> cmakeBuildTaskMap = new HashMap<>();
  private final Map<String, TaskProvider<Task>> cmakeCheckTaskMap = new HashMap<>();
  private final Map<String, TaskProvider<CMakeConfigureExec>> cmakeConfigureTaskMap = new HashMap<>();

  @Override
  public void apply(Project project) {
    project.allprojects(this::allprojects);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void allprojects(final Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.getName(), CMakeExtension.class);
  }

  private void afterEvaluate(final Project project) {
    project.getLogger().debug("%s: Evaluating Project...".formatted(project.getName()));
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      if (Objects.nonNull(extension)) {
        final CMakeResolvedBuild resolvedBuild = new CMakeResolvedBuild(project.getName());
        final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName();
        project.getTasks()
            .register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project), resolvedBuild);
        final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
        project.getTasks()
            .register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(project), resolvedBuild)
            .configure((task) -> {
              task.dependsOn(assembleConfigTaskName);
            });
        project.getTasks().named("assemble").configure((task) -> {
          task.dependsOn(assembleListsTaskName);
        });

        project.getLogger().debug("%s: Evaluating %d Toolchains..."
            .formatted(project.getName(), extension.getToolchains().size()));
        for (final CMakeToolchain toolchain : extension.getToolchains()) {
          if (Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().getOrNull())) {
            final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
            configureTasks(project, resolvedToolchain);
            resolvedBuild.put(resolvedToolchain.getName(), resolvedToolchain);
          }
        }

        project.getLogger().debug("%s: Evaluating %d Custom Tasks..."
            .formatted(project.getName(), extension.getCustomTasks().size()));
        for (final Map.Entry<String, String[]> customTask : extension.getCustomTasks().entrySet()) {
          for (final String toolchainName : customTask.getValue()) {
            if (resolvedBuild.getToolchains().containsKey(toolchainName)) {
              final CMakeResolvedToolchain resolvedToolchain = resolvedBuild.getToolchains().get(toolchainName);
              configureTasks(project, resolvedToolchain, customTask.getKey());
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
                    configureTasks(project, resolvedLibrary, buildTarget, projectModuleDependencies);
                    resolvedBuild.addAll(resolvedLibrary.getPrivateProjectModuleDependencies());
                    resolvedBuild.addAll(resolvedLibrary.getPublicProjectModuleDependencies());
                  }
                  if (resolvedLibrary.isBuildShared()) {
                    final String buildTarget = CMakeListsConventions.sharedLibraryTarget(resolvedLibrary.getName(),
                        resolvedToolchain, buildConfig);
                    configureTasks(project, resolvedLibrary, buildTarget, projectModuleDependencies);
                    resolvedBuild.addAll(resolvedLibrary.getPrivateProjectModuleDependencies());
                    resolvedBuild.addAll(resolvedLibrary.getPublicProjectModuleDependencies());
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
                final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new ArrayList<>();
                projectModuleDependencies.addAll(resolvedApplication.getPrivateProjectModuleDependencies());
                configureTasks(project, resolvedApplication, buildTarget, projectModuleDependencies);
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
                final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new ArrayList<>();
                projectModuleDependencies.addAll(resolvedTest.getPrivateProjectModuleDependencies());
                configureTasks(project, resolvedTest, buildTarget, projectModuleDependencies);
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

  private void configureTasks(final Project project, final CMakeResolvedToolchain resolvedToolchain) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(resolvedToolchain.getName());
    final TaskProvider<CMakeConfigureExec> cmakeConfigureTask = project.getTasks().register(cmakeConfigureTaskName,
        CMakeConfigureExec.class, resolvedToolchain);
    cmakeConfigureTask.configure((task) -> {
      task.dependsOn(CMakeTasksConventions.assembleListsTaskName());
    });
    project.getTasks().named("assemble").configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
    });
    cmakeConfigureTaskMap.put(cmakeConfigureTaskName, cmakeConfigureTask);
    final String cmakeToolchainBuildTaskName = CMakeTasksConventions.buildTaskName(resolvedToolchain.getName());
    final TaskProvider<Task> cmakeToolchainBuildTask = project.getTasks().register(cmakeToolchainBuildTaskName);
    cmakeToolchainBuildTask.configure((task) -> {
      task.setGroup(CMakeTasksConventions.GROUP_BUILD);
    });
    cmakeBuildTaskMap.put(cmakeToolchainBuildTaskName, cmakeToolchainBuildTask);
    final String cmakeToolchainCheckTaskName = CMakeTasksConventions.checkTaskName(resolvedToolchain.getName());
    final TaskProvider<Task> cmakeToolchainCheckTask = project.getTasks().register(cmakeToolchainCheckTaskName);
    cmakeToolchainCheckTask.configure((task) -> {
      task.setGroup(CMakeTasksConventions.GROUP_CHECK);
    });
    cmakeCheckTaskMap.put(cmakeToolchainCheckTaskName, cmakeToolchainCheckTask);
  }

  private void configureTasks(final Project project, final CMakeResolvedLibrary resolvedLibrary,
      final String buildTarget, final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(cmakeBuildTaskName,
        CMakeBuildExec.class, buildTarget, resolvedLibrary);
    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedLibrary.getToolchain().getName());
    cmakeBuildTask.configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
        setTaskDependencyOfProjectModuleDependencies(projectModuleDependencies, task);
    });
    final String cmakeToolchainBuildTaskName = CMakeTasksConventions
        .buildTaskName(resolvedLibrary.getToolchain().getName());
    cmakeBuildTaskMap.get(cmakeToolchainBuildTaskName)
        .configure((task) -> {
          task.dependsOn(cmakeBuildTaskName);
        });
    project.getTasks().named("build").configure((task) -> {
      task.dependsOn(cmakeBuildTaskName);
    });
    if (resolvedLibrary.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      final TaskProvider<CMakePackage> packageTask = project.getTasks().register(packageTaskName,
          CMakePackage.class, buildTarget, resolvedLibrary.getToolchain());
      packageTask.configure((task) -> {
        task.dependsOn(cmakeBuildTaskName);
      });
    }
  }

  private void configureTasks(final Project project, final CMakeResolvedApplication resolvedApplication,
      final String buildTarget, final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(cmakeBuildTaskName,
        CMakeBuildExec.class, buildTarget, resolvedApplication);
    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedApplication.getToolchain().getName());
    cmakeBuildTask.configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
        setTaskDependencyOfProjectModuleDependencies(projectModuleDependencies, task);
    });
    cmakeConfigureTaskMap.get(cmakeConfigureTaskName).configure((task) ->
        updateTaskMapConfiguration(projectModuleDependencies,project,task)
    );
    cmakeBuildTaskMap.get(CMakeTasksConventions.buildTaskName(resolvedApplication.getToolchain().getName()))
        .configure((task) -> {
          task.dependsOn(cmakeBuildTaskName);
        });
    project.getTasks().named("build").configure((task) -> {
      task.dependsOn(cmakeBuildTaskName);
    });
    if (resolvedApplication.isPackageBuildOutputs()) {
      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
      final TaskProvider<CMakePackage> packageTask = project.getTasks()
          .register(packageTaskName, CMakePackage.class, buildTarget, resolvedApplication.getToolchain());
      packageTask.configure((task) -> {
        task.dependsOn(cmakeBuildTaskName);
      });
    }
  }

  private void configureTasks(final Project project, final CMakeResolvedTest resolvedTest, final String buildTarget,
      final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
    final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(cmakeBuildTaskName,
        CMakeBuildExec.class, buildTarget, resolvedTest);
    final String cmakeConfigureTaskName = CMakeTasksConventions
        .configureTaskName(resolvedTest.getToolchain().getName());
    cmakeBuildTask.configure((task) -> {
      task.dependsOn(cmakeConfigureTaskName);
        setTaskDependencyOfProjectModuleDependencies(resolvedTest.getPrivateProjectModuleDependencies(), task);
    });
    cmakeConfigureTaskMap.get(cmakeConfigureTaskName).configure((task) ->
        updateTaskMapConfiguration(resolvedTest.getPrivateProjectModuleDependencies(),project,task)
    );
    final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(buildTarget);
    final TaskProvider<CMakeTestExec> cmakeTestTask = project.getTasks().register(cmakeTestTaskName,
        CMakeTestExec.class, buildTarget, resolvedTest);
    cmakeTestTask.configure((task) -> {
      task.dependsOn(cmakeBuildTaskName);
    });
    cmakeCheckTaskMap.get(CMakeTasksConventions.checkTaskName(resolvedTest.getToolchain().getName()))
        .configure((task) -> {
          task.dependsOn(cmakeBuildTaskName);
        });
    project.getTasks().named("check").configure((task) -> {
      task.dependsOn(cmakeTestTaskName);
    });
  }

  private void configureTasks(final Project project, final CMakeResolvedToolchain toolchain, final String name) {
    final String cmakeCustomTaskName = CMakeTasksConventions.customTaskName(name, toolchain.getName());
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName());
    project.getTasks().register(cmakeCustomTaskName, CMakeExec.class, toolchain)
        .configure((task) -> {
          task.dependsOn(cmakeConfigureTaskName);
        });
  }

    private <T extends Task> void setTaskDependencyOfProjectModuleDependencies(Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies, T task) {
        projectModuleDependencies.stream()
                .filter(pmd -> pmd.isBuildable())
                .map(pmd -> pmd.getBuildTaskName())
                .filter(taskName -> !task.getDependsOn().contains(taskName))
                .forEach(taskName -> task.dependsOn(taskName));
    }

    private <T extends Task> void updateTaskMapConfiguration(Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies, Project project, T task) {
        projectModuleDependencies.stream()
                .map(pmd -> pmd.getProjectName())
                .filter(moduleName -> !Objects.equals(moduleName, project.getName()))
                .forEach(moduleName -> task.mustRunAfter(moduleName));
    }
}


