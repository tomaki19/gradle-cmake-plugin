/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.Action;
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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageZip;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;

public class CMakePlugin implements Plugin<Project> {

  private final AdhocComponentWithVariants cmakeComponent;
  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtos = new HashMap<>();

  @javax.inject.Inject
  CMakePlugin(final SoftwareComponentFactory softwareComponentFactory) {
    this.cmakeComponent = softwareComponentFactory.adhoc("cmake");
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, customTaskProtos);
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

      /* ============ Tasks ============== */

      final CMakeTaskRegistry taskRegistry = new CMakeTaskRegistry(project.getTasks());

      final TaskProvider<CMakeClean> cleanListsTask = taskRegistry.cleanListsTask();
      taskRegistry.cleanTask().configure((task) -> task.dependsOn(cleanListsTask));

      final TaskProvider<CMakeAssemble> assembleListsTask = taskRegistry.assembleListsTask(
          toolchains, project);
      taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      for (final CMakeResolvedToolchain toolchain : toolchains) {

        Optional<TaskProvider<?>> buildAllToolchainTask = Optional.empty();
        if (toolchain.hasBinaries()) {
          buildAllToolchainTask = Optional.of(taskRegistry.buildAllToolchainTask(toolchain));
          buildAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_BUILD));
            taskRegistry.buildTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        Optional<TaskProvider<?>> checkAllToolchainTask = Optional.empty();
        if (toolchain.hasTests()) {
          checkAllToolchainTask = Optional.of(taskRegistry.checkAllToolchainTask(toolchain));
          checkAllToolchainTask.ifPresent((taskProvider) -> {
            taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_CHECK));
            taskRegistry.checkTask().configure((task) -> task.dependsOn(taskProvider));
          });
        }

        for (final String buildConfig : toolchain.getBuildConfigs()) {
          Optional<TaskProvider<?>> buildAllBuildConfigTask = Optional.empty();
          if (toolchain.hasBinaries()) {
            buildAllBuildConfigTask = Optional.of(taskRegistry.buildAllBuildConfigTask(toolchain,
                buildConfig));
            buildAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_BUILD));
              taskRegistry.buildTask().configure((task) -> task.dependsOn(taskProvider));
            });
          }

          Optional<TaskProvider<?>> checkAllBuildConfigTask = Optional.empty();
          if (toolchain.hasTests()) {
            checkAllBuildConfigTask = Optional.of(taskRegistry.checkAllBuildConfigTask(toolchain,
                buildConfig));
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.setGroup(CMakeTaskRegistry.GROUP_CHECK));
              taskRegistry.checkTask().configure((task) -> task.dependsOn(taskProvider));
            });
          }

          final TaskProvider<CMakeConfigure> configureTask = taskRegistry.configureTask(toolchain,
              buildConfig);
          configureTask.configure((task) -> {
            task.dependsOn(assembleListsTask);
          });
          final Directory moduleDirectory = CMakeFileConventions.targetConfigDirectory(
              project.getLayout().getBuildDirectory().get());

          if (customTaskProtos.containsKey(toolchain.getName())) {
            customTaskProtos.get(toolchain.getName()).forEach((taskProto, taskAction) -> {
              if (Objects.equals(buildConfig, taskProto.getBuildConfig())) {
                taskRegistry.customExecTask(taskProto).configure(taskAction);
              }
            });
          }

          for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
            final Configuration moduleDirectoriesConfiguration = CMakeTaskRegistry.createModuleDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration outputDirectoriesConfiguration = CMakeTaskRegistry.createOutputDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Set<File> localOutputDirectories = new HashSet<>();
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                moduleDirectoriesConfiguration.getDependencies()
                    .add(dependency.createModuleDirectoriesDependency(project, toolchain, buildConfig));
                outputDirectoriesConfiguration.getDependencies()
                    .add(dependency.createOutputDirectoriesDependency(project, toolchain, buildConfig));
              } else {
                localOutputDirectories.add(CMakeFileConventions.targetBinaryDirectory(
                    project.getLayout().getBuildDirectory().get(), dependency, toolchain, buildConfig).getAsFile());
              }
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
                library, toolchain, buildConfig, project);
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });

            configureTask.configure((task) -> {
              task.getInputs().files(moduleDirectoriesConfiguration);
            });

            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), moduleDirectoriesConfiguration,
                moduleDirectory);

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(
                library, toolchain, buildConfig);
            packageTask.configure((task) -> {
              task.getInputs().files(outputDirectoriesConfiguration);
              task.from(localOutputDirectories);
            });
          }

          for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
            final Configuration moduleDirectoriesConfiguration = CMakeTaskRegistry.createModuleDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration outputDirectoriesConfiguration = CMakeTaskRegistry.createOutputDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            final Set<File> localOutputDirectories = new HashSet<>();
            localOutputDirectories.add(outputDirectory.getAsFile());
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                moduleDirectoriesConfiguration.getDependencies()
                    .add(dependency.createModuleDirectoriesDependency(project, toolchain, buildConfig));
                outputDirectoriesConfiguration.getDependencies()
                    .add(dependency.createOutputDirectoriesDependency(project, toolchain, buildConfig));
              } else {
                localOutputDirectories.add(CMakeFileConventions.targetBinaryDirectory(
                    project.getLayout().getBuildDirectory().get(), dependency, toolchain, buildConfig).getAsFile());
              }
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
                library, toolchain, buildConfig, project);
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });

            configureTask.configure((task) -> {
              task.getInputs().files(moduleDirectoriesConfiguration);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(library,
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), moduleDirectoriesConfiguration,
                moduleDirectory, buildTask);
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), outputDirectoriesConfiguration,
                outputDirectory, buildTask);

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(
                library, toolchain, buildConfig);
            packageTask.configure((task) -> {
              task.dependsOn(buildTask);
              task.getInputs().files(outputDirectoriesConfiguration);
              task.from(localOutputDirectories);
            });
          }

          for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
            final Configuration moduleDirectoriesConfiguration = CMakeTaskRegistry.createModuleDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration outputDirectoriesConfiguration = CMakeTaskRegistry.createOutputDirectoriesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), library, toolchain, buildConfig);
            final Set<File> localOutputDirectories = new HashSet<>();
            localOutputDirectories.add(outputDirectory.getAsFile());
            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                moduleDirectoriesConfiguration.getDependencies()
                    .add(dependency.createModuleDirectoriesDependency(project, toolchain, buildConfig));
                outputDirectoriesConfiguration.getDependencies()
                    .add(dependency.createOutputDirectoriesDependency(project, toolchain, buildConfig));
              } else {
                localOutputDirectories.add(CMakeFileConventions.targetBinaryDirectory(
                    project.getLayout().getBuildDirectory().get(), dependency, toolchain, buildConfig).getAsFile());
              }
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
                library, toolchain, buildConfig, project);
            assembleListsTask.configure((task) -> {
              task.dependsOn(assembleModulesTask);
            });

            configureTask.configure((task) -> {
              task.getInputs().files(moduleDirectoriesConfiguration);
            });

            final TaskProvider<CMakeBuildLibrary> buildTask = taskRegistry.buildTask(library,
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), moduleDirectoriesConfiguration,
                moduleDirectory, buildTask);
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), outputDirectoriesConfiguration,
                outputDirectory, buildTask);

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(
                library, toolchain, buildConfig);
            packageTask.configure((task) -> {
              task.dependsOn(buildTask);
              task.getInputs().files(outputDirectoriesConfiguration);
              task.from(localOutputDirectories);
            });
          }

          for (final CMakeResolvedExecutable application : toolchain.getApplications()) {
            final Configuration moduleDirectoriesConfiguration = CMakeTaskRegistry.createModuleDirectoriesConfiguration(
                project.getConfigurations(), application, toolchain, buildConfig);
            final Configuration outputDirectoriesConfiguration = CMakeTaskRegistry.createOutputDirectoriesConfiguration(
                project.getConfigurations(), application, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), application, toolchain, buildConfig);
            final Set<File> localOutputDirectories = new HashSet<>();
            localOutputDirectories.add(outputDirectory.getAsFile());
            for (final CMakeResolvedProjectDependency dependency : application.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                moduleDirectoriesConfiguration.getDependencies()
                    .add(dependency.createModuleDirectoriesDependency(project, toolchain, buildConfig));
                outputDirectoriesConfiguration.getDependencies()
                    .add(dependency.createOutputDirectoriesDependency(project, toolchain, buildConfig));
              } else {
                localOutputDirectories.add(CMakeFileConventions.targetBinaryDirectory(
                    project.getLayout().getBuildDirectory().get(), dependency, toolchain, buildConfig).getAsFile());
              }
            }

            configureTask.configure((task) -> {
              task.getInputs().files(moduleDirectoriesConfiguration);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(application,
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), moduleDirectoriesConfiguration,
                moduleDirectory, buildTask);

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(
                application, toolchain, buildConfig);
            packageTask.configure((task) -> {
              task.dependsOn(buildTask);
              task.getInputs().files(outputDirectoriesConfiguration);
              task.from(localOutputDirectories);
            });
          }

          for (final CMakeResolvedExecutable test : toolchain.getTests()) {
            final Configuration moduleDirectoriesConfiguration = CMakeTaskRegistry.createModuleDirectoriesConfiguration(
                project.getConfigurations(), test, toolchain, buildConfig);
            final Configuration outputDirectoriesConfiguration = CMakeTaskRegistry.createOutputDirectoriesConfiguration(
                project.getConfigurations(), test, toolchain, buildConfig);
            final Directory outputDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory().get(), test, toolchain, buildConfig);
            final Set<File> localOutputDirectories = new HashSet<>();
            localOutputDirectories.add(outputDirectory.getAsFile());
            for (final CMakeResolvedProjectDependency dependency : test.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                moduleDirectoriesConfiguration.getDependencies()
                    .add(dependency.createModuleDirectoriesDependency(project, toolchain, buildConfig));
                outputDirectoriesConfiguration.getDependencies()
                    .add(dependency.createOutputDirectoriesDependency(project, toolchain, buildConfig));
              } else {
                localOutputDirectories.add(CMakeFileConventions.targetBinaryDirectory(
                    project.getLayout().getBuildDirectory().get(), dependency, toolchain, buildConfig).getAsFile());
              }
            }

            configureTask.configure((task) -> {
              task.getInputs().files(moduleDirectoriesConfiguration);
            });

            final TaskProvider<CMakeBuildExecutable> buildTask = taskRegistry.buildTask(test,
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), moduleDirectoriesConfiguration,
                moduleDirectory, buildTask);

            final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(test, toolchain,
                buildConfig);
            checkTask.configure((task) -> task.dependsOn(buildTask));
            checkAllToolchainTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });
            checkAllBuildConfigTask.ifPresent((taskProvider) -> {
              taskProvider.configure((task) -> task.dependsOn(checkTask));
            });

            final TaskProvider<CMakePackageZip> packageTask = taskRegistry.packageTask(
                test, toolchain, buildConfig);
            packageTask.configure((task) -> {
              task.dependsOn(buildTask);
              task.getInputs().files(outputDirectoriesConfiguration);
              task.from(localOutputDirectories);
            });
          }
        }
      }

    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e);
    }
  }
}
