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
import io.github.tomaki19.gradle.cmake.files.CMakeModuleFile;
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
  public static final String GROUP_INSTALL = "cmake install";

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
    return tasks.register(taskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project.getName(),
        project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get()),
        project.getLayout().getProjectDirectory());
  }

  public TaskProvider<CMakeAssemble> assembleModuleTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws FileNotFoundException {
    final String taskName = CMakeTasksConventions.assembleModuleTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    final CMakeModuleFile moduleFile = new CMakeModuleFile(library, toolchain, buildConfig, project.getName(),
        project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());
    return tasks.register(taskName, CMakeAssemble.class, moduleFile,
        project.getLayout().getBuildDirectory().get().dir(CMakeFileConventions.CMAKE_CONFIG_PATH));
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
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeBuildLibrary.class, library, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final TaskContainer tasks, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.buildTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeBuildExecutable.class, binary, toolchain, buildConfig);
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

  public TaskProvider<CMakeInstall> installTask(final TaskContainer tasks, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.installTaskName(executable.getName(), toolchain.getName(),
        buildConfig);
    return tasks.register(taskName, CMakeInstall.class, CMakeFileConventions.buildTarget(executable.getName(),
        toolchain.getName(), buildConfig), toolchain, buildConfig);
  }

  public TaskProvider<CMakeInstall> installTask(final TaskContainer tasks, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String taskName = CMakeTasksConventions.installTaskName(library.getName(), library.getLinkType(),
        toolchain.getName(), buildConfig);
    return tasks.register(taskName, CMakeInstall.class, CMakeFileConventions.buildTarget(library.getName(),
        library.getLinkType(), toolchain.getName(), buildConfig), toolchain, buildConfig);
  }

  public static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(library.getPrivateProjectDependencies());
    dependencies.addAll(library.getPublicProjectDependencies());
    configureRemote(task, project, toolchain, buildConfig, dependencies);
  }

  public static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, project, toolchain, buildConfig, executable.getPrivateProjectDependencies());
  }

  private static void configureRemote(final CMakeAssemble task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !Objects.equals(project.getName(), dependency.getProjectName()))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.assembleModuleTaskName(dependency.getProjectName(),
            dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig)));
  }

  public static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectDependency> dependencies = new ArrayList<>();
    dependencies.addAll(library.getPrivateProjectDependencies());
    dependencies.addAll(library.getPublicProjectDependencies());
    configureRemote(task, project, toolchain, buildConfig, dependencies);
  }

  public static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, project, toolchain, buildConfig, executable.getPrivateProjectDependencies());
  }

  private static void configureRemote(final CMakeConfigure task, final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) {
    dependencies.stream()
        .filter(dependency -> !Objects.equals(project.getName(), dependency.getProjectName()))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.configureTaskName(dependency.getProjectName(),
            toolchain.getName(), buildConfig)));
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
        .filter((dependency) -> !Objects.equals(dependency.getLinkType(), CMakeLinkType.INTERFACE))
        .forEach((dependency) -> task.dependsOn(CMakeTasksConventions.buildTaskName(
            dependency.getResolvedProject().getName(), dependency.getName(), dependency.getLinkType(),
            toolchain.getName(), buildConfig)));
  }

}
