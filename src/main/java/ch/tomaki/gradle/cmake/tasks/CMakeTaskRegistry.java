/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModule;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;

public class CMakeTaskRegistry {

  private final TaskContainer taskContainer;

  private final Map<String, TaskProvider<? extends Task>> cmakeTaskMap = new HashMap<>();

  private enum GradleTasks {
    ASSEMBLE("assemble"), BUILD("build"), CHECK("check");

    private final String name;

    private GradleTasks(final String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public CMakeTaskRegistry(final Project project) {
    this.taskContainer = project.getTasks();
  }

  public <T extends Task> TaskProvider<T> register(String name, Class<T> type, Object... constructorArgs)
      throws InvalidUserDataException {
    final TaskProvider<T> task = taskContainer.register(name, type, constructorArgs);
    cmakeTaskMap.put(name, task);
    return task;
  }

  public TaskProvider<Task> register(String name) throws InvalidUserDataException {
    final TaskProvider<Task> task = taskContainer.register(name);
    cmakeTaskMap.put(name, task);
    return task;
  }

  @SuppressWarnings("unchecked")
  public TaskProvider<Task> configure(final String name, final Action<? super Task> action) {
    return (TaskProvider<Task>) cmakeTaskMap.computeIfPresent(name, (key, value) -> {
      value.configure(action);
      return value;
    });
  }

  public void configureAssembleConfigTaskProjectModuleDependencies(final String taskName,
      final Collection<CMakeResolvedProjectModule> projectModules) {
    taskContainer.named(taskName).configure((task) -> {
      projectModules.stream()
          .map(dependency -> CMakeTasksConventions.assembleConfigTaskName(dependency.getProject().getName()))
          .forEach(assembleConfigTaskName -> task.dependsOn(assembleConfigTaskName));
    });
  }

  public void configureConfigureTaskProjectModuleDependencies(final String taskName,
      final Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    taskContainer.named(taskName).configure((task) -> {
      projectModuleDependencies.stream()
          .filter(dependency -> !Objects.equals(dependency.getProject(), task.getProject()))
          .filter(dependency -> !Objects.equals(dependency.getType(), CMakeLinkType.INTERFACE))
          .map(dependency -> CMakeTasksConventions.configureTaskName(dependency.getProject().getName(),
              dependency.getToolchain().getName()))
          .forEach(configTaskName -> task.mustRunAfter(configTaskName));
    });
  }

  public void configureBuildTaskProjectModuleDependencies(final String taskName,
      final Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    taskContainer.named(taskName).configure((task) -> {
      projectModuleDependencies.stream()
          .filter(dependency -> !Objects.equals(dependency.getType(), CMakeLinkType.INTERFACE))
          .map(dependency -> CMakeTasksConventions.buildTaskName(dependency.getProject().getName(),
              dependency.getBuildTarget()))
          .forEach(buildTaskName -> task.dependsOn(buildTaskName));
    });
  }

  public TaskProvider<Task> getGradleAssembleTask() {
    return taskContainer.named(GradleTasks.ASSEMBLE.toString());
  }

  public TaskProvider<Task> getGradleBuildTask() {
    return taskContainer.named(GradleTasks.BUILD.toString());
  }

  public TaskProvider<Task> getGradleCheckTask() {
    return taskContainer.named(GradleTasks.CHECK.toString());
  }
}
