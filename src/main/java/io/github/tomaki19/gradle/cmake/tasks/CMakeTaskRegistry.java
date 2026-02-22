/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
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
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTaskRegistry {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
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
    final File outputFile = project.getLayout().getProjectDirectory().file(CMakeListsFile.name()).getAsFile();
    return tasks.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project.getName(),
        project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get()), outputFile);
  }

  public TaskProvider<CMakeAssemble> assembleConfigTask(final TaskContainer tasks,
      final CMakeResolvedToolchain toolchain, final Project project) throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleConfigTaskName(toolchain.getName());
    final File outputFile = project.getLayout().getBuildDirectory().dir(CMakeFileConventions.CMAKE_EXPORT_PATH).get()
        .file(CMakeConfigFile.name(CMakeFileConventions.cmakeConfigName(project.getName(), toolchain.getName())))
        .getAsFile();
    return tasks.register(taskName, CMakeAssemble.class, new CMakeConfigFile(toolchain, project.getName(),
        project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get()), outputFile);
  }

  public TaskProvider<CMakeCustomExec> customExecTask(final TaskContainer tasks, final CMakeCustomTaskProto taskProto) {
    final String taskName = CMakeTasksConventions.customExecTaskName(taskProto.getName(), taskProto.getToolchainName(),
        taskProto.getBuildConfig());
    return tasks.register(taskName, CMakeCustomExec.class, taskProto.getToolchainName(),
        taskProto.getBuildConfig(), taskProto.getEnvironmentFile());
  }

  public TaskProvider<CMakeConfigure> configureTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String taskName = CMakeTasksConventions.configureTaskName(toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String linkage, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library.getName(), toolchain.getName(),
        linkage, buildConfig);
    return tasks.register(taskName, CMakeBuildLibrary.class, library, toolchain, linkage,
        buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final TaskContainer tasks, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeBuildExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakePackageLibrary> packageTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String linkage, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(library.getName(), toolchain.getName(),
        linkage, buildConfig);
    return tasks.register(taskName, CMakePackageLibrary.class, library, toolchain, linkage,
        buildConfig);
  }

  public TaskProvider<CMakePackageExecutable> packageTask(final TaskContainer tasks,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.packageTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakePackageExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakeCheck> checkTask(final TaskContainer tasks, final CMakeResolvedExecutable test,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.checkTaskName(test.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeCheck.class, test, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public TaskProvider<Task> checkAllToolchainTask(final TaskContainer tasks, final CMakeResolvedToolchain toolchain) {
    final String taskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
    return tasks.register(taskName);
  }

  public static void configureRemote(final CMakeConfigure task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) {
    toolchain.getProjects().stream()
        .filter(dependency -> !Objects.equals(project.getName(), dependency.getName()))
        .forEach(dependency -> {
          task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(dependency.getName(),
              toolchain.getName()));
          task.dependsOn(CMakeTasksConventions.configureTaskName(dependency.getName(),
              toolchain.getName(), buildConfig));
        });
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(library.getPrivateProjectDependencies());
    dependencies.addAll(library.getPublicProjectDependencies());
    configureRemote(task, toolchain, buildConfig, dependencies);
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, toolchain, buildConfig, executable.getPrivateProjectDependencies());
  }

  private static void configureRemote(final CMakeBuild task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !Objects.equals(dependency.getLinkage(), CMakeLinkType.INTERFACE.toString()))
        .forEach(
            dependency -> task.dependsOn(CMakeTasksConventions.buildTaskName(dependency.getResolvedProject().getName(),
                dependency.getName(), toolchain.getName(), dependency.getLinkage(), buildConfig)));
  }

}
