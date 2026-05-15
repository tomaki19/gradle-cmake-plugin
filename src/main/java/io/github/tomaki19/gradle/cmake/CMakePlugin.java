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

import io.github.tomaki19.gradle.cmake.extension.CMakeArtifactHandler;
import io.github.tomaki19.gradle.cmake.extension.CMakeConfigurationContainer;
import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.CMakeTaskContainer;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
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

      final TaskProvider<CMakeAssemble> assembleListsTask = tasks.assembleListsTask(
          toolchains, project);
      tasks.assembleTask().configure((task) -> task.dependsOn(assembleListsTask));

      final Directory moduleDirectory = CMakeFileConventions.targetConfigDirectory(
          project.getLayout().getBuildDirectory());
      for (final CMakeResolvedToolchain toolchain : toolchains) {
        registerToolchainTasks(toolchain, extension, tasks, configurations, artifacts,
            moduleDirectory, assembleListsTask, project);
      }
    } catch (GradleException e) {
      throw e;
    } catch (Exception e) {
      throw new GradleException(e.getMessage(), e);
    }
  }

  private void registerToolchainTasks(
      final CMakeResolvedToolchain toolchain,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final CMakeArtifactHandler artifacts,
      final Directory moduleDirectory,
      final TaskProvider<CMakeAssemble> assembleListsTask,
      final Project project) {

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

    extension.getTasks().applyExecTasks(toolchain);

    for (final String buildConfig : toolchain.getBuildConfigs()) {
      registerBuildConfigTasks(toolchain, buildConfig, buildAllToolchainTask, checkAllToolchainTask,
          extension, tasks, configurations, artifacts, moduleDirectory, assembleListsTask, project);
    }
  }

  private void registerBuildConfigTasks(
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Optional<TaskProvider<?>> buildAllToolchainTask,
      final Optional<TaskProvider<?>> checkAllToolchainTask,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final CMakeArtifactHandler artifacts,
      final Directory moduleDirectory,
      final TaskProvider<CMakeAssemble> assembleListsTask,
      final Project project) {

    Optional<TaskProvider<?>> buildAllBuildConfigTask = Optional.empty();
    if (toolchain.hasBinaries()) {
      buildAllBuildConfigTask = Optional.of(tasks.buildAllBuildConfigTask(toolchain, buildConfig));
      buildAllBuildConfigTask.ifPresent((taskProvider) -> {
        taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_BUILD));
        tasks.buildTask().configure((task) -> task.dependsOn(taskProvider));
      });
    }

    Optional<TaskProvider<?>> checkAllBuildConfigTask = Optional.empty();
    if (toolchain.hasTests()) {
      checkAllBuildConfigTask = Optional.of(tasks.checkAllBuildConfigTask(toolchain, buildConfig));
      checkAllBuildConfigTask.ifPresent((taskProvider) -> {
        taskProvider.configure((task) -> task.setGroup(CMakeTaskContainer.GROUP_CHECK));
        tasks.checkTask().configure((task) -> task.dependsOn(taskProvider));
      });
    }

    final TaskProvider<CMakeConfigure> configureTask = tasks.configureTask(toolchain, buildConfig);
    configureTask.configure((task) -> {
      task.dependsOn(assembleListsTask);
    });

    extension.getTasks().applyExecTasks(toolchain, buildConfig, (task) -> {
      task.dependsOn(configureTask);
    });

    for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
      registerInterfaceLibraryTasks(library, toolchain, buildConfig, configureTask,
          assembleListsTask, moduleDirectory, extension, tasks, configurations, artifacts, project);
    }

    for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
      registerBinaryLibraryTasks(library, toolchain, buildConfig, buildAllToolchainTask,
          buildAllBuildConfigTask, configureTask, assembleListsTask, moduleDirectory,
          extension, tasks, configurations, artifacts, project);
    }

    for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
      registerBinaryLibraryTasks(library, toolchain, buildConfig, buildAllToolchainTask,
          buildAllBuildConfigTask, configureTask, assembleListsTask, moduleDirectory,
          extension, tasks, configurations, artifacts, project);
    }

    for (final CMakeResolvedApplication application : toolchain.getApplications()) {
      registerApplicationTasks(application, toolchain, buildConfig, buildAllToolchainTask,
          buildAllBuildConfigTask, configureTask, extension, tasks, configurations, project);
    }

    for (final CMakeResolvedTest test : toolchain.getTests()) {
      registerTestTasks(test, toolchain, buildConfig, buildAllToolchainTask, buildAllBuildConfigTask,
          checkAllToolchainTask, checkAllBuildConfigTask, configureTask, extension, tasks,
          configurations, project);
    }
  }

  private void registerInterfaceLibraryTasks(
      final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final TaskProvider<CMakeConfigure> configureTask,
      final TaskProvider<CMakeAssemble> assembleListsTask,
      final Directory moduleDirectory,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final CMakeArtifactHandler artifacts,
      final Project project) {

    final Configuration modulesConfiguration = configurations.createModulesConfiguration(library, toolchain,
        buildConfig);
    final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(library, toolchain,
        buildConfig);
    final Configuration developConfiguration = configurations.createDevelopConfiguration(library, toolchain,
        buildConfig);

    registerProjectDependencies(library.getAllProjectDependencies(), modulesConfiguration,
        runtimeConfiguration, developConfiguration, project, toolchain, buildConfig);

    final TaskProvider<CMakeAssemble> assembleModulesTask = tasks.assembleModuleTask(moduleDirectory,
        library, toolchain, buildConfig, project);
    assembleListsTask.configure((task) -> {
      task.dependsOn(assembleModulesTask);
    });
    configureTask.configure((task) -> {
      task.getInputs().files(modulesConfiguration);
    });

    artifacts.addDirectoryArtifact(modulesConfiguration, moduleDirectory, configureTask);

    extension.getTasks().applyExecTasks(toolchain, buildConfig, library, (task) -> {
      task.dependsOn(configureTask);
    });
    extension.getTasks().applyDevelopArchiveTasks(toolchain, buildConfig, library, (task) -> {
      task.dependsOn(configureTask);
      task.getArchiveBaseName().set(CMakeFileConventions.buildTarget(library, toolchain, buildConfig));
      task.from(developConfiguration).into("lib");
      library.getHeaders().forEach((headers) -> task.from(headers).into("include"));
    });
  }

  private void registerBinaryLibraryTasks(
      final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Optional<TaskProvider<?>> buildAllToolchainTask,
      final Optional<TaskProvider<?>> buildAllBuildConfigTask,
      final TaskProvider<CMakeConfigure> configureTask,
      final TaskProvider<CMakeAssemble> assembleListsTask,
      final Directory moduleDirectory,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final CMakeArtifactHandler artifacts,
      final Project project) {

    final Configuration modulesConfiguration = configurations.createModulesConfiguration(library, toolchain,
        buildConfig);
    final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(library, toolchain,
        buildConfig);
    final Configuration developConfiguration = configurations.createDevelopConfiguration(library, toolchain,
        buildConfig);

    registerProjectDependencies(library.getAllProjectDependencies(), modulesConfiguration,
        runtimeConfiguration, developConfiguration, project, toolchain, buildConfig);

    final TaskProvider<CMakeAssemble> assembleModulesTask = tasks.assembleModuleTask(moduleDirectory,
        library, toolchain, buildConfig, project);
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

    extension.getTasks().applyExecTasks(toolchain, buildConfig, library, (task) -> {
      task.dependsOn(buildTask);
    });
    extension.getTasks().applyRuntimeArchiveTasks(toolchain, buildConfig, library, (task) -> {
      task.dependsOn(buildTask);
      task.getArchiveBaseName().set(CMakeFileConventions.buildTarget(library, toolchain, buildConfig));
      task.from(runtimeConfiguration);
      task.from(libraryDirectory);
    });
    extension.getTasks().applyDevelopArchiveTasks(toolchain, buildConfig, library, (task) -> {
      task.dependsOn(buildTask);
      task.getArchiveBaseName().set(CMakeFileConventions.buildTarget(library, toolchain, buildConfig));
      task.from(developConfiguration).into("lib");
      library.getHeaders().forEach((headers) -> task.from(headers).into("include"));
    });
  }

  private void registerApplicationTasks(
      final CMakeResolvedApplication application,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Optional<TaskProvider<?>> buildAllToolchainTask,
      final Optional<TaskProvider<?>> buildAllBuildConfigTask,
      final TaskProvider<CMakeConfigure> configureTask,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final Project project) {

    final Configuration modulesConfiguration = configurations.createModulesConfiguration(
        application, toolchain, buildConfig);
    final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(
        application, toolchain, buildConfig);
    final Configuration developConfiguration = configurations.createDevelopConfiguration(
        application, toolchain, buildConfig);

    registerProjectDependencies(application.getAllProjectDependencies(), modulesConfiguration,
        runtimeConfiguration, developConfiguration, project, toolchain, buildConfig);

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

    extension.getTasks().applyExecTasks(toolchain, buildConfig, application, (task) -> {
      task.dependsOn(buildTask);
    });
    extension.getTasks().applyRuntimeArchiveTasks(toolchain, buildConfig, application, (task) -> {
      task.dependsOn(buildTask);
      task.getArchiveBaseName().set(CMakeFileConventions.buildTarget(application, toolchain, buildConfig));
      task.from(runtimeConfiguration);
      task.from(CMakeFileConventions.targetBinaryDirectory(project.getLayout().getBuildDirectory(), application,
          toolchain, buildConfig));
    });
  }

  private void registerTestTasks(
      final CMakeResolvedTest test,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Optional<TaskProvider<?>> buildAllToolchainTask,
      final Optional<TaskProvider<?>> buildAllBuildConfigTask,
      final Optional<TaskProvider<?>> checkAllToolchainTask,
      final Optional<TaskProvider<?>> checkAllBuildConfigTask,
      final TaskProvider<CMakeConfigure> configureTask,
      final CMakeExtension extension,
      final CMakeTaskContainer tasks,
      final CMakeConfigurationContainer configurations,
      final Project project) {

    final Configuration modulesConfiguration = configurations.createModulesConfiguration(test, toolchain,
        buildConfig);
    final Configuration runtimeConfiguration = configurations.createRuntimeConfiguration(test, toolchain,
        buildConfig);
    final Configuration developConfiguration = configurations.createDevelopConfiguration(test, toolchain,
        buildConfig);

    registerProjectDependencies(test.getAllProjectDependencies(), modulesConfiguration,
        runtimeConfiguration, developConfiguration, project, toolchain, buildConfig);

    configureTask.configure((task) -> {
      task.getInputs().files(modulesConfiguration);
    });

    final TaskProvider<CMakeBuildExecutable> buildTask = tasks.buildTask(test, toolchain, buildConfig);
    buildTask.configure((task) -> {
      task.dependsOn(configureTask);
    });
    buildAllToolchainTask.ifPresent((taskProvider) -> {
      taskProvider.configure((task) -> task.dependsOn(buildTask));
    });
    buildAllBuildConfigTask.ifPresent((taskProvider) -> {
      taskProvider.configure((task) -> task.dependsOn(buildTask));
    });

    final TaskProvider<CMakeCheck> checkTask = tasks.checkTask(test, toolchain, buildConfig);
    checkTask.configure((task) -> task.dependsOn(buildTask));
    checkAllToolchainTask.ifPresent((taskProvider) -> {
      taskProvider.configure((task) -> task.dependsOn(checkTask));
    });
    checkAllBuildConfigTask.ifPresent((taskProvider) -> {
      taskProvider.configure((task) -> task.dependsOn(checkTask));
    });

    extension.getTasks().applyExecTasks(toolchain, buildConfig, test, (task) -> {
      task.dependsOn(buildTask);
    });
    extension.getTasks().applyRuntimeArchiveTasks(toolchain, buildConfig, test, (task) -> {
      task.dependsOn(buildTask);
      task.getArchiveBaseName().set(CMakeFileConventions.buildTarget(test, toolchain, buildConfig));
      task.from(runtimeConfiguration);
      task.from(CMakeFileConventions.targetBinaryDirectory(project.getLayout().getBuildDirectory(), test, toolchain,
          buildConfig));
    });
  }

  private void registerProjectDependencies(
      final Collection<CMakeResolvedProjectDependency> dependencies,
      final Configuration modulesConfiguration,
      final Configuration runtimeConfiguration,
      final Configuration developConfiguration,
      final Project project,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    for (final CMakeResolvedProjectDependency dependency : dependencies) {
      if (dependency.isRemote()) {
        modulesConfiguration.getDependencies()
            .add(dependency.createModulesDependency(project, toolchain, buildConfig));
      }
      runtimeConfiguration.getDependencies()
          .add(dependency.createRuntimeDependency(project, toolchain, buildConfig));
      developConfiguration.getDependencies()
          .add(dependency.createDevelopDependency(project, toolchain, buildConfig));
    }
  }
}
