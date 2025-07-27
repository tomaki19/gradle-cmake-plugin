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

import io.github.tomaki19.gradle.cmake.files.CMakeConfigFile;
import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
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
    final String assembleListsTaskName = CMakeTasksConventions.assembleListsTaskName();
    return taskContainer.register(assembleListsTaskName, CMakeAssemble.class, new CMakeListsFile(toolchains, project));
  }

  public TaskProvider<CMakeAssemble> assembleConfigTask(final CMakeResolvedToolchain toolchain, final Project project)
      throws FileNotFoundException {
    final String assembleConfigTaskName = CMakeTasksConventions.assembleConfigTaskName(toolchain.getName());
    return taskContainer.register(assembleConfigTaskName, CMakeAssemble.class, new CMakeConfigFile(project, toolchain));
  }

  public TaskProvider<CMakeConfigure> configureTask(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String cmakeConfigureTaskName = CMakeTasksConventions.configureTaskName(toolchain.getName(), buildConfig);
    return taskContainer.register(cmakeConfigureTaskName, CMakeConfigure.class, toolchain, buildConfig);
  }

  public TaskProvider<CMakeBuildLibrary> buildTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType linkType, final String buildConfig) {
    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(library.getName(), toolchain.getName(),
        linkType, buildConfig);
    return taskContainer.register(cmakeBuildTaskName, CMakeBuildLibrary.class, library, toolchain, linkType,
        buildConfig);
  }

  public TaskProvider<CMakeBuildExecutable> buildTask(final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String cmakeBuildTaskName = CMakeTasksConventions.buildTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(cmakeBuildTaskName, CMakeBuildExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakePackageLibrary> packageTask(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType linkType, final String buildConfig) {
    final String packageTaskName = CMakeTasksConventions.packageTaskName(library.getName(), toolchain.getName(),
        linkType, buildConfig);
    return taskContainer.register(packageTaskName, CMakePackageLibrary.class, library, toolchain, linkType,
        buildConfig);
  }

  public TaskProvider<CMakePackageExecutable> packageTask(final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String packageTaskName = CMakeTasksConventions.packageTaskName(binary.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(packageTaskName, CMakePackageExecutable.class, binary, toolchain, buildConfig);
  }

  public TaskProvider<CMakeCheck> checkTask(final CMakeResolvedExecutable test, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String cmakeCheckTaskName = CMakeTasksConventions.checkTaskName(test.getName(), toolchain.getName(),
        buildConfig);
    return taskContainer.register(cmakeCheckTaskName, CMakeCheck.class, test, toolchain, buildConfig);
  }

  public TaskProvider<Task> buildAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String cmakeToolchainBuildAllTaskName = CMakeTasksConventions.buildAllTaskName(toolchain.getName());
    return taskContainer.register(cmakeToolchainBuildAllTaskName);
  }

  public TaskProvider<Task> checkAllToolchainTask(final CMakeResolvedToolchain toolchain) {
    final String cmakeToolchainCheckAllTaskName = CMakeTasksConventions.checkAllTaskName(toolchain.getName());
    return taskContainer.register(cmakeToolchainCheckAllTaskName);
  }

  public static void configureRemote(final CMakeConfigure task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) {
    toolchain.getProjectPackages().stream()
        .filter(dependency -> !Objects.equals(project, dependency.getProject()))
        .forEach(dependency -> {
          task.mustRunAfter(CMakeTasksConventions.configureTaskName(dependency.getProject(),
              toolchain.getName(), buildConfig));
          task.dependsOn(CMakeTasksConventions.assembleConfigTaskName(dependency.getProject(),
              toolchain.getName()));
        });
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Collection<CMakeResolvedProjectPackageDependency> dependencies = new ArrayList<>();
    dependencies.addAll(library.getPrivateProjectPackageDependencies());
    dependencies.addAll(library.getPublicProjectPackageDependencies());
    configureRemote(task, toolchain, buildConfig, dependencies);
  }

  public static void configureRemote(final CMakeBuild task, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    configureRemote(task, toolchain, buildConfig, executable.getPrivateProjectPackageDependencies());
  }

  private static void configureRemote(final CMakeBuild task, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectPackageDependency> dependencies) {
    dependencies.stream().filter(dependency -> !Objects.equals(dependency.getType(), CMakeLinkType.INTERFACE))
        .forEach(dependency -> task.dependsOn(CMakeTasksConventions.buildTaskName(dependency.getProject(),
            dependency.getName(), toolchain.getName(), dependency.getType(), buildConfig)));
  }

}
