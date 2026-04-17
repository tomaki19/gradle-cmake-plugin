/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomPackageTaskProto;
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
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageDevelopment;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageRuntime;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTasksConventions;

public class CMakePlugin implements Plugin<Project> {

  private final AdhocComponentWithVariants cmakeComponent;
  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtos = new HashMap<>();
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> customPackageRuntimeTaskProtos = new HashMap<>();
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> customPackageDevelopmentTaskProtos = new HashMap<>();

  @javax.inject.Inject
  CMakePlugin(final SoftwareComponentFactory softwareComponentFactory) {
    this.cmakeComponent = softwareComponentFactory.adhoc(CMakeExtension.NAME);
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, customTaskProtos,
        customPackageRuntimeTaskProtos, customPackageDevelopmentTaskProtos);
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

      final Directory moduleDirectory = CMakeFileConventions.targetConfigDirectory(
          project.getLayout().getBuildDirectory());

      final TaskProvider<CMakeAssemble> assembleListsTask = taskRegistry.assembleListsTask(
          toolchains, project);
      assembleListsTask.configure((task) -> {
        task.getOutputDirectory().set(project.getLayout().getProjectDirectory());
      });
      taskRegistry.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      final Map<String, TaskProvider<CMakePackageRuntime>> createdRuntimePackageTasks = new HashMap<>();
      final Map<String, TaskProvider<CMakePackageDevelopment>> createdDevelopmentPackageTasks = new HashMap<>();

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

          if (customTaskProtos.containsKey(toolchain.getName())) {
            customTaskProtos.get(toolchain.getName()).forEach((taskProto, taskAction) -> {
              if (Objects.equals(buildConfig, taskProto.getBuildConfig())) {
                taskRegistry.customExecTask(taskProto).configure(taskAction);
              }
            });
          }

          for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
            final Configuration modulesConfiguration = CMakeTaskRegistry.createModulesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration developConfiguration = CMakeTaskRegistry.createDevelopConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);

            for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
              if (dependency.isRemote()) {
                modulesConfiguration.getDependencies()
                    .add(dependency.createModulesDependency(project, toolchain, buildConfig));
              }
              developConfiguration.getDependencies()
                  .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
            }

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
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

            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), modulesConfiguration,
                moduleDirectory, configureTask);

            if (customPackageDevelopmentTaskProtos.containsKey(toolchain.getName())) {
              configureDevelopmentPackageForLibrary(
                  customPackageDevelopmentTaskProtos.get(toolchain.getName()),
                  createdDevelopmentPackageTasks, taskRegistry, library, toolchain, buildConfig,
                  developConfiguration, null, configureTask);
            }
          }

          for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
            final Configuration modulesConfiguration = CMakeTaskRegistry.createModulesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration runtimeConfiguration = CMakeTaskRegistry.createRuntimeConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration developConfiguration = CMakeTaskRegistry.createDevelopConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);

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

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), modulesConfiguration,
                moduleDirectory, buildTask);
            final Directory libraryDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), library, toolchain, buildConfig);
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), runtimeConfiguration,
                libraryDirectory, buildTask);

            if (customPackageDevelopmentTaskProtos.containsKey(toolchain.getName())) {
              configureDevelopmentPackageForLibrary(
                  customPackageDevelopmentTaskProtos.get(toolchain.getName()),
                  createdDevelopmentPackageTasks, taskRegistry, library, toolchain, buildConfig,
                  developConfiguration, libraryDirectory, buildTask);
            }
          }

          for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
            final Configuration modulesConfiguration = CMakeTaskRegistry.createModulesConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration runtimeConfiguration = CMakeTaskRegistry.createRuntimeConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);
            final Configuration developConfiguration = CMakeTaskRegistry.createDevelopConfiguration(
                project.getConfigurations(), library, toolchain, buildConfig);

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

            final TaskProvider<CMakeAssemble> assembleModulesTask = taskRegistry.assembleModuleTask(
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
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), modulesConfiguration,
                moduleDirectory, buildTask);
            final Directory libraryDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), library, toolchain, buildConfig);
            CMakeTaskRegistry.addDirectoryArtifact(project.getArtifacts(), runtimeConfiguration,
                libraryDirectory, buildTask);

            if (customPackageRuntimeTaskProtos.containsKey(toolchain.getName())) {
              configureRuntimePackageForLibrary(
                  customPackageRuntimeTaskProtos.get(toolchain.getName()),
                  createdRuntimePackageTasks, taskRegistry, library, toolchain, buildConfig,
                  runtimeConfiguration, libraryDirectory, buildTask);
            }
            if (customPackageDevelopmentTaskProtos.containsKey(toolchain.getName())) {
              configureDevelopmentPackageForLibrary(
                  customPackageDevelopmentTaskProtos.get(toolchain.getName()),
                  createdDevelopmentPackageTasks, taskRegistry, library, toolchain, buildConfig,
                  developConfiguration, libraryDirectory, buildTask);
            }
          }

          for (final CMakeResolvedExecutable application : toolchain.getApplications()) {
            final Configuration modulesConfiguration = CMakeTaskRegistry.createModulesConfiguration(
                project.getConfigurations(), application, toolchain, buildConfig);
            final Configuration runtimeConfiguration = CMakeTaskRegistry.createRuntimeConfiguration(
                project.getConfigurations(), application, toolchain, buildConfig);
            final Configuration developConfiguration = CMakeTaskRegistry.createDevelopConfiguration(
                project.getConfigurations(), application, toolchain, buildConfig);

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

            final Directory applicationDirectory = CMakeFileConventions.targetBinaryDirectory(
                project.getLayout().getBuildDirectory(), application, toolchain, buildConfig);
            if (customPackageRuntimeTaskProtos.containsKey(toolchain.getName())) {
              configureRuntimePackageForExecutable(
                  customPackageRuntimeTaskProtos.get(toolchain.getName()),
                  createdRuntimePackageTasks, taskRegistry, application, toolchain, buildConfig,
                  buildTask, runtimeConfiguration, applicationDirectory);
            }
            if (customPackageDevelopmentTaskProtos.containsKey(toolchain.getName())) {
              configureDevelopmentPackageForExecutable(
                  customPackageDevelopmentTaskProtos.get(toolchain.getName()),
                  createdDevelopmentPackageTasks, taskRegistry, application, toolchain, buildConfig,
                  buildTask, developConfiguration, applicationDirectory);
            }
          }

          for (final CMakeResolvedExecutable test : toolchain.getTests()) {
            final Configuration modulesConfiguration = CMakeTaskRegistry.createModulesConfiguration(
                project.getConfigurations(), test, toolchain, buildConfig);
            final Configuration runtimeConfiguration = CMakeTaskRegistry.createRuntimeConfiguration(
                project.getConfigurations(), test, toolchain, buildConfig);
            final Configuration developConfiguration = CMakeTaskRegistry.createDevelopConfiguration(
                project.getConfigurations(), test, toolchain, buildConfig);

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

            final TaskProvider<CMakeCheck> checkTask = taskRegistry.checkTask(test, toolchain,
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
            if (customPackageRuntimeTaskProtos.containsKey(toolchain.getName())) {
              configureRuntimePackageForExecutable(
                  customPackageRuntimeTaskProtos.get(toolchain.getName()),
                  createdRuntimePackageTasks, taskRegistry, test, toolchain, buildConfig,
                  buildTask, runtimeConfiguration, testDirectory);
            }
            if (customPackageDevelopmentTaskProtos.containsKey(toolchain.getName())) {
              configureDevelopmentPackageForExecutable(
                  customPackageDevelopmentTaskProtos.get(toolchain.getName()),
                  createdDevelopmentPackageTasks, taskRegistry, test, toolchain, buildConfig,
                  buildTask, developConfiguration, testDirectory);
            }
          }
        }
      }
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e);
    }
  }

  private void configureRuntimePackageForLibrary(
      final Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>> protos,
      final Map<String, TaskProvider<CMakePackageRuntime>> createdTasks,
      final CMakeTaskRegistry taskRegistry,
      final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Configuration runtimeConfiguration,
      final Directory binaryDirectory,
      final Object... dependencyTasks) {

    protos.forEach((proto, action) -> {
      if (!proto.getBuildConfig().equals(buildConfig))
        return;
      if (!proto.matchesComponent(library.getName()))
        return;

      final String taskName = CMakeTasksConventions.customPackageTaskName(proto.getName(), toolchain, buildConfig);

      final TaskProvider<CMakePackageRuntime> pkgTask = createdTasks.computeIfAbsent(taskName, n -> {
        final TaskProvider<CMakePackageRuntime> t = taskRegistry.customPackageRuntimeTask(n, proto.getName());
        t.configure(action);
        return t;
      });

      pkgTask.configure(task -> {
        task.dependsOn(dependencyTasks);
        task.from(runtimeConfiguration);
        task.from(binaryDirectory);
      });
    });
  }

  private void configureDevelopmentPackageForLibrary(
      final Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>> protos,
      final Map<String, TaskProvider<CMakePackageDevelopment>> createdTasks,
      final CMakeTaskRegistry taskRegistry,
      final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Configuration developConfiguration,
      final Directory binaryDirectory,
      final Object... dependencyTasks) {

    protos.forEach((proto, action) -> {
      if (!proto.getBuildConfig().equals(buildConfig))
        return;
      if (!proto.matchesComponent(library.getName()))
        return;

      final String taskName = CMakeTasksConventions.customPackageTaskName(proto.getName(), toolchain, buildConfig);

      final TaskProvider<CMakePackageDevelopment> pkgTask = createdTasks.computeIfAbsent(taskName, n -> {
        final TaskProvider<CMakePackageDevelopment> t = taskRegistry.customPackageDevelopmentTask(n, proto.getName());
        t.configure(action);
        return t;
      });

      pkgTask.configure(task -> {
        task.dependsOn(dependencyTasks);
        task.from(developConfiguration);
        if (binaryDirectory != null) {
          task.from(binaryDirectory);
        }
        for (final File headerDir : library.getHeaders()) {
          task.from(headerDir);
        }
      });
    });
  }

  private void configureRuntimePackageForExecutable(
      final Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>> protos,
      final Map<String, TaskProvider<CMakePackageRuntime>> createdTasks,
      final CMakeTaskRegistry taskRegistry,
      final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final TaskProvider<CMakeBuildExecutable> buildTask,
      final Configuration runtimeConfiguration,
      final Directory binaryDirectory) {

    protos.forEach((proto, action) -> {
      if (!proto.getBuildConfig().equals(buildConfig))
        return;
      if (!proto.matchesComponent(executable.getName()))
        return;

      final String taskName = CMakeTasksConventions.customPackageTaskName(proto.getName(), toolchain, buildConfig);

      final TaskProvider<CMakePackageRuntime> pkgTask = createdTasks.computeIfAbsent(taskName, n -> {
        final TaskProvider<CMakePackageRuntime> t = taskRegistry.customPackageRuntimeTask(n, proto.getName());
        t.configure(action);
        return t;
      });

      pkgTask.configure(task -> {
        task.dependsOn(buildTask);
        task.from(runtimeConfiguration);
        task.from(binaryDirectory);
      });
    });
  }

  private void configureDevelopmentPackageForExecutable(
      final Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>> protos,
      final Map<String, TaskProvider<CMakePackageDevelopment>> createdTasks,
      final CMakeTaskRegistry taskRegistry,
      final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final TaskProvider<CMakeBuildExecutable> buildTask,
      final Configuration developConfiguration,
      final Directory binaryDirectory) {

    protos.forEach((proto, action) -> {
      if (!proto.getBuildConfig().equals(buildConfig))
        return;
      if (!proto.matchesComponent(executable.getName()))
        return;

      final String taskName = CMakeTasksConventions.customPackageTaskName(proto.getName(), toolchain, buildConfig);

      final TaskProvider<CMakePackageDevelopment> pkgTask = createdTasks.computeIfAbsent(taskName, n -> {
        final TaskProvider<CMakePackageDevelopment> t = taskRegistry.customPackageDevelopmentTask(n, proto.getName());
        t.configure(action);
        return t;
      });

      pkgTask.configure(task -> {
        task.dependsOn(buildTask);
        task.from(developConfiguration);
        task.from(binaryDirectory);
      });
    });
  }
}
