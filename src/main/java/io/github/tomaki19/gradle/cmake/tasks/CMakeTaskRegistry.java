/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.files.CMakeConfigFile;
import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeTaskRegistry {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_PACKAGE = "cmake package";

  private final TaskContainer taskContainer;

  public CMakeTaskRegistry(final TaskContainer tasks) {
    this.taskContainer = tasks;
  }

  public TaskProvider<Task> assembleTask() {
    return taskContainer.named("assemble");
  }

  public TaskProvider<Task> buildTask() {
    return taskContainer.named("build");
  }

  public TaskProvider<Task> checkTask() {
    return taskContainer.named("check");
  }

  public TaskProvider<Task> cleanTask() {
    return taskContainer.named("clean");
  }

  public TaskProvider<CMakeAssemble> assembleListsTask(final Collection<CMakeResolvedToolchain> toolchains,
      final Project project) throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleListsTaskName();
    return taskContainer.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project));
  }

  public TaskProvider<CMakeAssemble> assembleConfigTask(final CMakeResolvedToolchain toolchain, final Project project)
      throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleConfigTaskName(toolchain.getName());
    return taskContainer.register(taskName, CMakeAssemble.class, new CMakeConfigFile(toolchain, project));
  }

  public TaskProvider<CMakeCustomExec> customExecTask(final CMakeCustomTaskProto taskProto) {
    final String taskName = CMakeTasksConventions.customExecTaskName(taskProto.getName(), taskProto.getToolchain(),
        taskProto.getBuildConfig());
    return taskContainer.register(taskName, CMakeCustomExec.class, taskProto.getToolchain(),
        taskProto.getBuildConfig());
  }

  public TaskProvider<CMakeConfigure> configureTask(final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.configureTaskName(toolchain.getName(), buildConfig);
    return taskContainer.register(taskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType linkType, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library.getName(), toolchain.getName(),
        linkType, buildConfig);
    return taskContainer.register(taskName, CMakeBuildLibrary.class, library, toolchain, linkType,
        buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(taskName, CMakeBuildExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakePackageLibrary> packageTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType linkType, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(library.getName(), toolchain.getName(),
        linkType, buildConfig);
    return taskContainer.register(taskName, CMakePackageLibrary.class, library, toolchain, linkType,
        buildConfig);
  }

  public TaskProvider<CMakePackageExecutable> packageTask(final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(taskName, CMakePackageExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakeCheck> checkTask(final CMakeResolvedExecutable test, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkTaskName(test.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(taskName, CMakeCheck.class, test, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    return taskContainer.register(taskName);
  }

  public TaskProvider<Task> checkAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
    return taskContainer.register(taskName);
  }

  public static void configureRemote(final CMakeConfigure task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) {
    toolchain.getProjects().stream()
        .filter(dependency -> !Objects.equals(project.getName(), dependency.getName()))
        .forEach(dependency -> {
          task.mustRunAfter(CMakeTasksConventions.configureTaskName(dependency.getName(),
              toolchain.getName(), buildConfig));
          task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(dependency.getName(),
              toolchain.getName()));
        });
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(library.getPrivateProjectPackageDependencies());
    dependencies.addAll(library.getPublicProjectDependencies());
    configureRemote(task, toolchain, buildConfig, dependencies);
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, toolchain, buildConfig, executable.getPrivateProjectPackageDependencies());
  }

  private static void configureRemote(final CMakeBuild task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream().filter(dependency -> !Objects.equals(dependency.getType(), CMakeLinkType.INTERFACE))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.buildTaskName(dependency.getProject().getName(),
            dependency.getName(), toolchain.getName(), dependency.getType(), buildConfig)));
  }

}
