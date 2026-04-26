/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import java.util.Collection;
import java.util.Optional;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeArtifactHandler;
import io.github.tomaki19.gradle.cmake.model.CMakeConfigurationContainer;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskContainer;

public class CMakePlugin implements Plugin<Project> {

  private final AdhocComponentWithVariants cmakeComponent;

  @javax.inject.Inject
  CMakePlugin(final SoftwareComponentFactory softwareComponentFactory) {
    this.cmakeComponent = softwareComponentFactory.adhoc(CMakeExtension.NAME);
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, project.getTasks());
    project.getComponents().add(cmakeComponent);
    project.afterEvaluate(this::afterEvaluate);
  }

  private void afterEvaluate(final Project project) {
    try {

      /* ============ Resolve ============ */
      final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());
      final CMakeConfigurationContainer configurations = new CMakeConfigurationContainer(project.getConfigurations());
      final CMakeArtifactHandler artifacts = new CMakeArtifactHandler(project.getArtifacts());
      final CMakeTaskContainer tasks = new CMakeTaskContainer(project.getTasks());

      /* ============ Tasks ============== */

      final TaskProvider<CMakeClean> cleanListsTask = tasks.cleanListsTask();
      tasks.cleanTask().configure((task) -> task.dependsOn(cleanListsTask));

      final Directory moduleDirectory = CMakeFileConventions.targetConfigDirectory(
          project.getLayout().getBuildDirectory());

      final TaskProvider<CMakeAssemble> assembleListsTask = tasks.assembleListsTask(
          toolchains, project);
      assembleListsTask.configure((task) -> {
        task.getOutputDirectory().set(project.getLayout().getProjectDirectory());
      });
      tasks.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      for (final CMakeResolvedToolchain toolchain : toolchains) {

        Optional<TaskProvider<?>> buildAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          buildAllToolchainTask = Optional.of(tasks.buildAllToolchainTask(toolchain));
          buildAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_BUILD));
            tasks.buildTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        Optional<TaskProvider<?>> checkAllToolchainTask = Optional.empty();
        if (toolchain.hasTests()) {
          checkAllToolchainTask = Optional.of(tasks.checkAllToolchainTask(toolchain));
          checkAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_CHECK));
            tasks.checkTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        extension.getTasks().applyExecTask(toolchain);

        for (final String buildConfig : toolchain.getBuildConfigs()) {
          Optional<TaskProvider<?>> buildAllBuildConfigTask = Optional.empty();
          if (toolchain.hasBinaries()) {
            buildAllBuildConfigTask = Optional.of(tasks.buildAllBuildConfigTask(toolchain,
                buildConfig));
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_BUILD));
              tasks.buildTask().configure((task) -> task.dependsOn(taskProvider));
            });
          }

          Optional<TaskProvider<?>> checkAllBuildConfigTask = Optional.empty();
          if (toolchain.hasTests()) {
            checkAllBuildConfigTask = Optional.of(tasks.checkAllBuildConfigTask(toolchain,
                buildConfig));
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_CHECK));
              tasks.checkTask().configure((task) -> task.dependsOn(taskProvider));
            });
          }

          final TaskProvider<CMakeConfigure> configureTask = tasks.configureTask(toolchain,
              buildConfig);
          configureTask.configure((task) -> {
            task.dependsOn(assembleListsTask);
          });

          extension.getTasks().applyExecTask(toolchain, buildConfig, (task) -> {
            task.dependsOn(configureTask);
          });

          for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
            final Configuration modulesConfiguration = configurations.createModulesConfiguration(library, toolchain,
                buildConfig);
            final Configuration developConfiguration = configurations.createDevelopConfiguration(library, toolchain,
                buildConfig);

            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = tasks.assembleModuleTask(
                library, toolchain, buildConfig, project);
            assembleModulesTask.configure((task) -> {
              task.getOutputDirectory().set(moduleDirectory);
            });
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });
            configureTask.configure((task) -> {
              task.getInputs().files(modulesConfiguration);
            });

            artifacts.addDirectoryArtifact(modulesConfiguration, moduleDirectory, configureTask);

            extension.getTasks().applyExecTask(toolchain, buildConfig, library, (task) -> {
              task.dependsOn(configureTask);
            });
            extension.getTasks().applyDevelopPackageTask(toolchain, buildConfig, library,
                (task) -> {
                  task.dependsOn(configureTask);
                  task.from(developConfiguration).into("lib");
                  library.getHeaders().forEach((headers) -> task.from(headers).into("include"));
                });
          }

          for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
            final Configuration modulesConfiguration = configurations.createModulesConfiguration(library, toolchain,
                buildConfig);
            final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(library, toolchain,
                buildConfig);
            final Configuration developConfiguration = configurations.createDevelopConfiguration(library, toolchain,
                buildConfig);

            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              runtimeConfiguration.getDependencies()
                  .add(dependency.createRuntimeDependency(project, toolchain, buildConfig));
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = tasks.assembleModuleTask(library, toolchain,
                buildConfig, project);
            assembleModulesTask.configure((task) -> {
              task.getOutputDirectory().set(moduleDirectory);
            });
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });
            configureTask.configure((task) -> {
              task.getInputs().files(modulesConfiguration);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = tasks.buildTask(library, toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            artifacts.addDirectoryArtifact(modulesConfiguration, moduleDirectory, buildTask);
            final Directory libraryDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), library, toolchain, buildConfig);
            artifacts.addDirectoryArtifact(runtimeConfiguration, libraryDirectory, buildTask);

            extension.getTasks().applyExecTask(toolchain, buildConfig, library, (task) -> {
              task.dependsOn(buildTask);
            });
            extension.getTasks().applyRuntimePackageTask(toolchain, buildConfig, library,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(runtimeConfiguration);
                  task.from(libraryDirectory);
                });
            extension.getTasks().applyDevelopPackageTask(toolchain, buildConfig, library,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(developConfiguration).into("lib");
                  library.getHeaders().forEach((headers) -> task.from(headers).into("include"));
                });

          }

          for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
            final Configuration modulesConfiguration = configurations.createModulesConfiguration(library, toolchain,
                buildConfig);
            final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(library, toolchain,
                buildConfig);
            final Configuration developConfiguration = configurations.createDevelopConfiguration(library, toolchain,
                buildConfig);

            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              runtimeConfiguration.getDependencies()
                  .add(dependency.createRuntimeDependency(project, toolchain, buildConfig));
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = tasks.assembleModuleTask(library, toolchain,
                buildConfig, project);
            assembleModulesTask.configure((task) -> {
              task.getOutputDirectory().set(moduleDirectory);
            });
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });
            configureTask.configure((task) -> {
              task.getInputs().files(modulesConfiguration);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = tasks.buildTask(library, toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            artifacts.addDirectoryArtifact(modulesConfiguration,
                moduleDirectory, buildTask);
            final Directory libraryDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), library, toolchain, buildConfig);
            artifacts.addDirectoryArtifact(runtimeConfiguration, libraryDirectory, buildTask);

            extension.getTasks().applyExecTask(toolchain, buildConfig, library, (task) -> {
              task.dependsOn(buildTask);
            });
            extension.getTasks().applyRuntimePackageTask(toolchain, buildConfig, library,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(runtimeConfiguration);
                  task.from(libraryDirectory);
                });
            extension.getTasks().applyDevelopPackageTask(toolchain, buildConfig, library,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(developConfiguration).into("lib");
                  library.getHeaders().forEach((headers) -> task.from(headers).into("include"));
                });
          }

          for (final CMakeResolvedApplication application : toolchain.getApplications()) {
            final Configuration modulesConfiguration = configurations.createModulesConfiguration(
                application, toolchain, buildConfig);
            final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(
                application, toolchain, buildConfig);
            final Configuration developConfiguration = configurations.createDevelopConfiguration(
                application, toolchain, buildConfig);

            for (final CMakeResolvedProjectDependency dependency : application.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              runtimeConfiguration.getDependencies()
                  .add(dependency.createRuntimeDependency(project, toolchain, buildConfig));
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            configureTask.configure((task) -> {
              task.getInputs().files(modulesConfiguration);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = tasks.buildTask(application, toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final Directory applicationDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), application, toolchain, buildConfig);

            extension.getTasks().applyExecTask(toolchain, buildConfig, application, (task) -> {
              task.dependsOn(buildTask);
            });
            extension.getTasks().applyRuntimePackageTask(toolchain, buildConfig, application,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(runtimeConfiguration);
                  task.from(applicationDirectory);
                });
          }

          for (final CMakeResolvedTest test : toolchain.getTests()) {
            final Configuration modulesConfiguration = configurations.createModulesConfiguration(test, toolchain,
                buildConfig);
            final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(test, toolchain,
                buildConfig);
            final Configuration developConfiguration = configurations.createDevelopConfiguration(test, toolchain,
                buildConfig);

            for (final CMakeResolvedProjectDependency dependency : test.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              runtimeConfiguration.getDependencies()
                  .add(dependency.createRuntimeDependency(project, toolchain, buildConfig));
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            configureTask.configure((task) -> {
              task.getInputs().files(modulesConfiguration);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = tasks.buildTask(test,
                toolchain, buildConfig);
            buildTask.configure((task) -> {
              task.dependsOn(configureTask);
            });
            buildAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(buildTask));
            });

            final TaskProvider<CMakeCheck> checkTask = tasks.checkTask(test, toolchain,
                buildConfig);
            checkTask.configure((task) -> task.dependsOn(buildTask));
            checkAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });

            final Directory testDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), test, toolchain, buildConfig);

            extension.getTasks().applyExecTask(toolchain, buildConfig, test, (task) -> {
              task.dependsOn(buildTask);
            });
            extension.getTasks().applyRuntimePackageTask(toolchain, buildConfig, test,
                (task) -> {
                  task.dependsOn(buildTask);
                  task.from(runtimeConfiguration);
                  task.from(testDirectory);
                });
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e);
    }
  }
}
