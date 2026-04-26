/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import java.io.FileNotFoundException;
import java.util.Collection;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.files.CMakeModuleFile;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTasksConventions;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;

public final class CMakeTaskContainer {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_INSTALL = "cmake install";
  public static final String GROUP_PACKAGE = "cmake package";

  private final TaskContainer tasks;

  public CMakeTaskContainer(final TaskContainer tasks) {
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

}
