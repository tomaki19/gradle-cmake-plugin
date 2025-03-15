
package ch.tomaki.gradle.cmake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extensions.CMakeBinary;
import ch.tomaki.gradle.cmake.extensions.CMakeExtension;
import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extensions.CMakeLibrary;
import ch.tomaki.gradle.cmake.extensions.CMakeTest;
import ch.tomaki.gradle.cmake.extensions.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedFindPackage;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import ch.tomaki.gradle.cmake.tasks.CMakeAssembleConfig;
import ch.tomaki.gradle.cmake.tasks.CMakeAssembleLists;
import ch.tomaki.gradle.cmake.tasks.CMakeBuildExec;
import ch.tomaki.gradle.cmake.tasks.CMakeConfigureExec;
import ch.tomaki.gradle.cmake.tasks.CMakePackage;
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTestExec;

public class CMakePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.allprojects(CMakePlugin::allprojects);
    project.afterEvaluate(CMakePlugin::afterEvaluate);
  }

  private static void allprojects(final Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.getName(), CMakeExtension.class);
  }

  private static void afterEvaluate(final Project project) {
    project.getLogger().debug("%s: Evaluating Project..."
        .formatted(project.getName()));
    try {
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      if (Objects.nonNull(extension)) {
        final CMakeResolvedBuild resolvedBuild = new CMakeResolvedBuild(project.getName());

        final TaskProvider<Task> assembleTask = project.getTasks().named("assemble");
        final TaskProvider<Task> buildTask = project.getTasks().named("build");
        final Map<String, TaskProvider<Task>> cmakeGlobalBuildTaskMap = new HashMap<>();
        final TaskProvider<Task> checkTask = project.getTasks().named("check");
        final Map<String, TaskProvider<Task>> cmakeGlobalCheckTaskMap = new HashMap<>();

        final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName();
        project.getTasks().register(assembleConfigTaskName, CMakeAssembleConfig.class, resolvedBuild);
        assembleTask.configure((task) -> {
          task.dependsOn(assembleConfigTaskName);
        });

        final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
        final TaskProvider<CMakeAssembleLists> assembleListsTask = project.getTasks().register(assembleListsTaskName,
            CMakeAssembleLists.class, resolvedBuild);
        assembleListsTask.configure((task) -> {
          task.dependsOn(assembleConfigTaskName);
        });
        assembleTask.configure((task) -> {
          task.dependsOn(assembleListsTaskName);
        });

        project.getLogger().debug("%s: Evaluating %d Toolchains..."
            .formatted(project.getName(), extension.getToolchains().size()));
        final Map<String, TaskProvider<CMakeConfigureExec>> cmakeConfigureTaskMap = new HashMap<>();
        for (final CMakeToolchain toolchain : extension.getToolchains()) {
          if (Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().getOrNull())) {
            final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
            final String cmakeConfigureTaskName = CMakeTasksConventions
                .configureToolchainTaskName(resolvedToolchain.getName());
            final TaskProvider<CMakeConfigureExec> cmakeConfigureTask = project.getTasks()
                .register(cmakeConfigureTaskName, CMakeConfigureExec.class, resolvedToolchain);
            cmakeConfigureTask.configure((task) -> {
              task.dependsOn(assembleListsTaskName);
            });
            assembleTask.configure((task) -> {
              task.dependsOn(cmakeConfigureTaskName);
            });
            cmakeConfigureTaskMap.put(cmakeConfigureTaskName, cmakeConfigureTask);
            final String cmakeToolchainBuildTaskName = CMakeTasksConventions.buildTaskName(resolvedToolchain.getName());
            final TaskProvider<Task> cmakeToolchainBuildTask = project.getTasks()
                .register(cmakeToolchainBuildTaskName);
            cmakeToolchainBuildTask.configure((task) -> {
              task.setGroup(CMakeTasksConventions.GROUP_BUILD);
            });
            cmakeGlobalBuildTaskMap.put(cmakeToolchainBuildTaskName, cmakeToolchainBuildTask);
            final String cmakeToolchainCheckTaskName = CMakeTasksConventions.checkTaskName(resolvedToolchain.getName());
            final TaskProvider<Task> cmakeToolchainCheckTask = project.getTasks()
                .register(cmakeToolchainCheckTaskName);
            cmakeToolchainCheckTask.configure((task) -> {
              task.setGroup(CMakeTasksConventions.GROUP_CHECK);
            });
            cmakeGlobalCheckTaskMap.put(cmakeToolchainCheckTaskName, cmakeToolchainCheckTask);
            resolvedBuild.put(resolvedToolchain.getName(), resolvedToolchain);
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
              final String cmakeConfigureTaskName = CMakeTasksConventions
                  .configureToolchainTaskName(resolvedToolchain.getName());
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library,
                    extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
                final List<CMakeResolvedProjectModuleDependency> projectModuleDependencies = new ArrayList<>();
                projectModuleDependencies.addAll(resolvedLibrary.getPrivateProjectModuleDependencies());
                projectModuleDependencies.addAll(resolvedLibrary.getPublicProjectModuleDependencies());
                if (!resolvedLibrary.getSources().isEmpty()) {
                  if (resolvedLibrary.isBuildStatic()) {
                    final String buildTarget = CMakeListsConventions.staticLibraryTarget(resolvedLibrary.getName(),
                        resolvedToolchain, buildConfig);
                    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
                    final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(
                        cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedLibrary);
                    cmakeBuildTask.configure((task) -> {
                      task.dependsOn(cmakeConfigureTaskName);
                      for (final CMakeResolvedProjectModuleDependency projectModule : projectModuleDependencies) {
                        final String projectModuleBuildTaskName = projectModule.getBuildTaskName();
                        if (projectModule.isBuildable() && !task.getDependsOn()
                            .contains(projectModuleBuildTaskName)) {
                          task.dependsOn(projectModuleBuildTaskName);
                        }
                      }
                    });
                    cmakeGlobalBuildTaskMap.get(CMakeTasksConventions.buildTaskName(toolchainName))
                        .configure((task) -> {
                          task.dependsOn(cmakeBuildTaskName);
                        });
                    buildTask.configure((task) -> {
                      task.dependsOn(cmakeBuildTaskName);
                    });
                    if (resolvedLibrary.isPackageBuildOutputs()) {
                      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
                      final TaskProvider<CMakePackage> packageTask = project.getTasks().register(packageTaskName,
                          CMakePackage.class, buildTarget, resolvedToolchain);
                      packageTask.configure((task) -> {
                        task.dependsOn(cmakeBuildTaskName);
                      });
                    }
                    resolvedBuild.addAll(resolvedLibrary.getPrivateProjectModuleDependencies());
                    resolvedBuild.addAll(resolvedLibrary.getPublicProjectModuleDependencies());
                  }
                  if (resolvedLibrary.isBuildShared()) {
                    final String buildTarget = CMakeListsConventions.sharedLibraryTarget(resolvedLibrary.getName(),
                        resolvedToolchain, buildConfig);
                    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
                    final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(
                        cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedLibrary);
                    cmakeBuildTask.configure((task) -> {
                      task.dependsOn(cmakeConfigureTaskName);
                      for (final CMakeResolvedProjectModuleDependency projectModule : projectModuleDependencies) {
                        final String projectModuleBuildTaskName = projectModule.getBuildTaskName();
                        if (projectModule.isBuildable() && !task.getDependsOn()
                            .contains(projectModuleBuildTaskName)) {
                          task.dependsOn(projectModuleBuildTaskName);
                        }
                      }
                    });
                    cmakeConfigureTaskMap.get(cmakeConfigureTaskName).configure((task) -> {
                      for (final CMakeResolvedProjectModuleDependency projectModule : projectModuleDependencies) {
                        if (!Objects.equals(projectModule.getProjectName(), project.getName())) {
                          task.mustRunAfter(projectModule.getConfigTaskName());
                        }
                      }
                    });
                    cmakeGlobalBuildTaskMap.get(CMakeTasksConventions.buildTaskName(toolchainName))
                        .configure((task) -> {
                          task.dependsOn(cmakeBuildTaskName);
                        });
                    buildTask.configure((task) -> {
                      task.dependsOn(cmakeBuildTaskName);
                    });
                    if (resolvedLibrary.isPackageBuildOutputs()) {
                      final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
                      final TaskProvider<CMakePackage> packageTask = project.getTasks().register(packageTaskName,
                          CMakePackage.class, buildTarget, resolvedToolchain);
                      packageTask.configure((task) -> {
                        task.dependsOn(cmakeBuildTaskName);
                      });
                    }
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
              final String cmakeConfigureTaskName = CMakeTasksConventions
                  .configureToolchainTaskName(resolvedToolchain.getName());
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedApplication resolvedApplication = new CMakeResolvedApplication(application,
                    extension.getFindPackages().getAsMap(),
                    resolvedToolchain, buildConfig, project);
                final String buildTarget = CMakeListsConventions.applicationTarget(resolvedApplication.getName(),
                    resolvedToolchain, buildConfig);
                final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
                final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(
                    cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedApplication);
                cmakeBuildTask.configure((task) -> {
                  task.dependsOn(cmakeConfigureTaskName);
                  for (final CMakeResolvedProjectModuleDependency projectModule : resolvedApplication
                      .getPrivateProjectModuleDependencies()) {
                    final String projectModuleBuildTaskName = projectModule.getBuildTaskName();
                    if (projectModule.isBuildable() && !task.getDependsOn()
                        .contains(projectModuleBuildTaskName)) {
                      task.dependsOn(projectModuleBuildTaskName);
                    }
                  }
                });
                cmakeConfigureTaskMap.get(cmakeConfigureTaskName).configure((task) -> {
                  for (final CMakeResolvedProjectModuleDependency projectModule : resolvedApplication
                      .getPrivateProjectModuleDependencies()) {
                    if (!Objects.equals(projectModule.getProjectName(), project.getName())) {
                      task.mustRunAfter(projectModule.getConfigTaskName());
                    }
                  }
                });
                cmakeGlobalBuildTaskMap.get(CMakeTasksConventions.buildTaskName(toolchainName))
                    .configure((task) -> {
                      task.dependsOn(cmakeBuildTaskName);
                    });
                buildTask.configure((task) -> {
                  task.dependsOn(cmakeBuildTaskName);
                });
                if (resolvedApplication.isPackageBuildOutputs()) {
                  final String packageTaskName = CMakeTasksConventions.packageTaskName(buildTarget);
                  final TaskProvider<CMakePackage> packageTask = project.getTasks().register(packageTaskName,
                      CMakePackage.class, buildTarget, resolvedToolchain);
                  packageTask.configure((task) -> {
                    task.dependsOn(cmakeBuildTaskName);
                  });
                }
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
              final String cmakeConfigureTaskName = CMakeTasksConventions
                  .configureToolchainTaskName(resolvedToolchain.getName());
              for (final String buildConfig : resolvedToolchain.getBuildConfigs()) {
                final CMakeResolvedTest resolvedTest = new CMakeResolvedTest(test,
                    extension.getFindPackages().getAsMap(), resolvedToolchain, buildConfig, project);
                resolvedTest.addLibraryDependencies(resolvedToolchain.getPrivateLinkDependencies(),
                    extension.getFindPackages().getAsMap(), project);
                final String buildTarget = CMakeListsConventions.testTarget(resolvedTest.getName(), resolvedToolchain,
                    buildConfig);
                final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(buildTarget);
                final TaskProvider<CMakeBuildExec> cmakeBuildTask = project.getTasks().register(
                    cmakeBuildTaskName, CMakeBuildExec.class, buildTarget, resolvedTest);
                cmakeBuildTask.configure((task) -> {
                  task.dependsOn(cmakeConfigureTaskName);
                  for (final CMakeResolvedProjectModuleDependency projectModule : resolvedTest
                      .getPrivateProjectModuleDependencies()) {
                    final String projectModuleBuildTaskName = projectModule.getBuildTaskName();
                    if (projectModule.isBuildable() &&
                        !task.getDependsOn().contains(projectModuleBuildTaskName)) {
                      task.dependsOn(projectModuleBuildTaskName);
                    }
                  }
                });
                cmakeConfigureTaskMap.get(cmakeConfigureTaskName).configure((task) -> {
                  for (final CMakeResolvedProjectModuleDependency projectModule : resolvedTest
                      .getPrivateProjectModuleDependencies()) {
                    if (!Objects.equals(projectModule.getProjectName(), project.getName())) {
                      task.mustRunAfter(projectModule.getConfigTaskName());
                    }
                  }
                });
                final String cmakeTestTaskName = CMakeTasksConventions.checkTaskName(buildTarget);
                final TaskProvider<CMakeTestExec> cmakeTestTask = project.getTasks().register(cmakeTestTaskName,
                    CMakeTestExec.class, buildTarget, resolvedTest);
                cmakeTestTask.configure((task) -> {
                  task.dependsOn(cmakeBuildTaskName);
                });
                cmakeGlobalCheckTaskMap.get(CMakeTasksConventions.checkTaskName(toolchainName)).configure((task) -> {
                  task.dependsOn(cmakeBuildTaskName);
                });
                checkTask.configure((task) -> {
                  task.dependsOn(cmakeTestTaskName);
                });
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

}
