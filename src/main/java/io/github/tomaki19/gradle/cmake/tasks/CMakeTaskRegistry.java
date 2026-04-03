/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.attributes.Category;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.files.CMakeModuleFile;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeArtifactAttributes;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTaskRegistry {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_INSTALL = "cmake install";
  public static final String GROUP_PACKAGE = "cmake package";

  public TaskProvider<Task> assembleTask(final TaskContainer tasks) {
    return tasks.named("assemble");
  }

  public TaskProvider<Task> buildTask(final TaskContainer tasks) {
    return tasks.named("build");
  }

  public TaskProvider<Task> checkTask(final TaskContainer tasks) {
    return tasks.named("check");
  }

  public TaskProvider<Task> cleanTask(final TaskContainer tasks) {
    return tasks.named("clean");
  }

  public TaskProvider<CMakeClean> cleanListsTask(final TaskContainer tasks) {
    final String taskName = CMakeTasksConventions.cleanListsTaskName();
    return tasks.register(taskName, CMakeClean.class);
  }

  public TaskProvider<CMakeAssemble> assembleListsTask(final TaskContainer tasks,
      final Collection<CMakeResolvedToolchain> toolchains, final Project project) throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleListsTaskName();
    return tasks.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project),
        project.getLayout().getProjectDirectory());
  }

  public TaskProvider<CMakeAssemble> assembleModuleTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleModuleTaskName(library, toolchain, buildConfig);
    final CMakeModuleFile moduleFile = new CMakeModuleFile(library, toolchain, buildConfig, project);
    return tasks.register(taskName, CMakeAssemble.class, moduleFile,
        project.getLayout().getBuildDirectory().get().dir(CMakeFileConventions.CMAKE_CONFIG_PATH));
  }

  public TaskProvider<CMakeCustomExec> customExecTask(final TaskContainer tasks, final CMakeCustomTaskProto taskProto) {
    final String taskName = CMakeTasksConventions.customExecTaskName(taskProto.getName(), taskProto.getToolchain(),
        taskProto.getBuildConfig());
    return tasks.register(taskName, CMakeCustomExec.class, taskProto.getToolchain(),
        taskProto.getBuildConfig(), taskProto.getEnvironmentFile());
  }

  public TaskProvider<CMakeConfigure> configureTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.configureTaskName(toolchain, buildConfig);
    return tasks.register(taskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.buildAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> buildAllBuildConfigTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library, toolchain, buildConfig);
    return tasks.register(taskName, CMakeBuildLibrary.class, library, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final TaskContainer tasks,
      final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(executable, toolchain, buildConfig);
    return tasks.register(taskName, CMakeBuildExecutable.class, executable, toolchain, buildConfig);
  }

  public TaskProvider<Task> checkAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.checkAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> checkAllBuildConfigTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeCheck> checkTask(final TaskContainer tasks, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkTaskName(executable, toolchain, buildConfig);
    return tasks.register(taskName, CMakeCheck.class, executable, toolchain, buildConfig);
  }

  public TaskProvider<CMakePackageZip> packageTask(final TaskContainer tasks, final Directory buildDirectory,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(library, toolchain, buildConfig);
    final Directory directory = CMakeFileConventions.targetBinaryDirectory(buildDirectory, library, toolchain,
        buildConfig);
    final TaskProvider<CMakePackageZip> provider = tasks.register(taskName, CMakePackageZip.class, directory);
    provider.configure((task) -> {
      task.getArchiveBaseName().set("%s-%s-%s-%s".formatted(library.getOutputName(),
          library.getLinkVariant().toLowerCase(), toolchain.getName(), buildConfig));
    });
    return provider;
  }

  public TaskProvider<CMakePackageZip> packageTask(final TaskContainer tasks, final Directory buildDirectory,
      final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(executable, toolchain, buildConfig);
    final Directory directory = CMakeFileConventions.targetBinaryDirectory(buildDirectory, executable, toolchain,
        buildConfig);
    final TaskProvider<CMakePackageZip> provider = tasks.register(taskName, CMakePackageZip.class, directory);
    provider.configure((task) -> {
      task.getArchiveBaseName().set("%s-%s-%s".formatted(executable.getOutputName(), toolchain.getName(), buildConfig));
    });
    return provider;
  }

  public static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(binary.getPrivateProjectDependencies());
    dependencies.addAll(binary.getPublicProjectDependencies());
    configureRemote(task, project, toolchain, buildConfig, dependencies);
  }

  private static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !dependency.equals(project))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.assembleModuleTaskName(
            dependency, toolchain, buildConfig)));
  }

  public static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, project, toolchain, buildConfig, binary.getAllProjectDependencies());
  }

  private static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !dependency.equals(project))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.configureTaskName(
            dependency, toolchain, buildConfig)));
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter((dependency) -> !Objects.equals(dependency.getLinkVariant(), CMakeLinkVariant.INTERFACE))
        .forEach((dependency) -> {
          task.dependsOn(CMakeTasksConventions.buildTaskName(dependency, toolchain, buildConfig));
        });
  }

  public static Configuration createDependencyConfiguration(final Project project,
      final CMakeResolvedProjectDependency dependency, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String target = CMakeFileConventions.moduleTarget(dependency, toolchain, buildConfig);
    final Optional<Configuration> oldConfiguration = Optional
        .ofNullable(project.getConfigurations().findByName(target));
    if (oldConfiguration.isPresent()) {
      return oldConfiguration.get();
    }
    return project.getConfigurations().create(target, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(true);
      newConfiguration.setCanBeResolved(true);
      newConfiguration.setCanBeConsumed(false);
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LINK_VARIANT_ATTRIBUTE,
          dependency.getLinkVariant().toString());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.TOOLCHAIN_ATTRIBUTE, toolchain.getName());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.BUILD_CONFIG_ATTRIBUTE, buildConfig);
      newConfiguration.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
          project.getObjects().named(Category.class, CMakeArtifactAttributes.CATEGORY));
    });
  }

  public static Configuration createDirectoryConfiguration(final Project project, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeFileConventions.moduleTarget(project, library, toolchain, buildConfig);
    return project.getConfigurations().create(target, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(false);
      newConfiguration.setCanBeResolved(false);
      newConfiguration.setCanBeConsumed(true);
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.LINK_VARIANT_ATTRIBUTE,
          library.getLinkVariant().toString());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.TOOLCHAIN_ATTRIBUTE, toolchain.getName());
      newConfiguration.getAttributes().attribute(CMakeArtifactAttributes.BUILD_CONFIG_ATTRIBUTE, buildConfig);
      newConfiguration.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE,
          project.getObjects().named(Category.class, CMakeArtifactAttributes.CATEGORY));
    });
  }

  public static void addOutputDirectoryArtifact(final ArtifactHandler artifacts, final Configuration configuration,
      final Directory outputDirectory, final TaskProvider<? extends CMakeBuild> buildTask) {
    artifacts.add(configuration.getName(), outputDirectory, (artifact) -> {
      artifact.builtBy(buildTask);
      artifact.setType(ArtifactTypeDefinition.DIRECTORY_TYPE);
    });
  }

}
