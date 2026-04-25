/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.files.CMakeModuleFile;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeConfigurationConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTaskRegistry {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_INSTALL = "cmake install";
  public static final String GROUP_PACKAGE = "cmake package";

  private final TaskContainer tasks;

  public CMakeTaskRegistry(final TaskContainer tasks) {
    this.tasks = tasks;
  }

  public TaskProvider<Task> assembleTask() {
    return tasks.named("assemble");
  }

  public TaskProvider<Task> buildTask() {
    return tasks.named("build");
  }

  public TaskProvider<Task> checkTask() {
    return tasks.named("check");
  }

  public TaskProvider<Task> cleanTask() {
    return tasks.named("clean");
  }

  public TaskProvider<CMakeClean> cleanListsTask() {
    final String taskName = CMakeTasksConventions.cleanListsTaskName();
    return tasks.register(taskName, CMakeClean.class);
  }

  public TaskProvider<CMakeAssemble> assembleListsTask(
      final Collection<CMakeResolvedToolchain> toolchains, final Project project) throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleListsTaskName();
    return tasks.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project));
  }

  public TaskProvider<CMakeAssemble> assembleModuleTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleModuleTaskName(library, toolchain, buildConfig);
    return tasks.register(taskName, CMakeAssemble.class, new CMakeModuleFile(library, toolchain, buildConfig, project));
  }

  public TaskProvider<CMakeCustomRuntimeZip> customPackageRuntimeTask(final String taskName, final String baseName) {
    final TaskProvider<CMakeCustomRuntimeZip> provider = tasks.register(taskName, CMakeCustomRuntimeZip.class);
    provider.configure((task) -> task.getArchiveBaseName().set(baseName));
    return provider;
  }

  public TaskProvider<CMakeCustomDevelopZip> customPackageDevelopmentTask(final String taskName,
      final String baseName) {
    final TaskProvider<CMakeCustomDevelopZip> provider = tasks.register(taskName, CMakeCustomDevelopZip.class);
    provider.configure((task) -> task.getArchiveBaseName().set(baseName));
    return provider;
  }

  public TaskProvider<CMakeCustomExec> customExecTask(final String name, final String toolchainName,
      final String buildConfig, final Optional<RegularFile> environmentFile) {
    final String taskName = CMakeTasksConventions.customExecTaskName(name, toolchainName, buildConfig);
    return tasks.register(taskName, CMakeCustomExec.class, toolchainName, buildConfig, environmentFile);
  }

  public TaskProvider<CMakeConfigure> configureTask(final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.configureTaskName(toolchain, buildConfig);
    return tasks.register(taskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.buildAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> buildAllBuildConfigTask(final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library, toolchain, buildConfig);
    return tasks.register(taskName, CMakeBuildLibrary.class, library, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(
      final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(executable, toolchain, buildConfig);
    return tasks.register(taskName, CMakeBuildExecutable.class, executable, toolchain, buildConfig);
  }

  public TaskProvider<Task> checkAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.checkAllToolchainTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> checkAllBuildConfigTask(final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkAllBuildConfigTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName);
  }

  public TaskProvider<CMakeCheck> checkTask(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkTaskName(executable, toolchain, buildConfig);
    return tasks.register(taskName, CMakeCheck.class, executable, toolchain, buildConfig);
  }

  public static Configuration createModulesConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createModulesName(executable, toolchain,
        buildConfig);
    return createInConfiguration(configurations, target);
  }

  public static Configuration createModulesConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createModulesName(library, toolchain, buildConfig);
    return createInConfiguration(configurations, target);
  }

  public static Configuration createRuntimeConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createRuntimeName(executable, toolchain, buildConfig);
    return createInConfiguration(configurations, target);
  }

  public static Configuration createRuntimeConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createRuntimeName(library, toolchain, buildConfig);
    return createInConfiguration(configurations, target);
  }

  public static Configuration createDevelopConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createDevelopName(executable, toolchain, buildConfig);
    return createInConfiguration(configurations, target);
  }

  public static Configuration createDevelopConfiguration(final ConfigurationContainer configurations,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeConfigurationConventions.createDevelopName(library, toolchain, buildConfig);
    return createInConfiguration(configurations, target);
  }

  private static Configuration createInConfiguration(final ConfigurationContainer configurations, final String target) {
    return configurations.create(target, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(true);
      newConfiguration.setCanBeResolved(true);
      newConfiguration.setCanBeConsumed(true);
    });
  }

  public static PublishArtifact addDirectoryArtifact(final ArtifactHandler artifacts, final Configuration configuration,
      final Directory outputDirectory, final Object... builtBy) {
    return artifacts.add(configuration.getName(), outputDirectory, (artifact) -> {
      artifact.builtBy(builtBy);
    });
  }

}
