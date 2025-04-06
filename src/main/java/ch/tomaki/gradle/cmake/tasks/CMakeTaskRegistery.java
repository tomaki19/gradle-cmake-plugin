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

import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;

public class CMakeTaskRegistery {

  private final String projectName;
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

  public CMakeTaskRegistery(final Project project) {
    this.projectName = project.getName();
    this.taskContainer = project.getTasks();
  }

  public String getProjectName() {
    return projectName;
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

  public <T extends Task> void configureTaskProjectModuleConfigureDependencies(final String taskName,
      final Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    taskContainer.named(taskName).configure((task) -> {
      projectModuleDependencies.parallelStream()
          .filter(dependency -> !Objects.equals(dependency.getProjectName(), projectName))
          .map(dependency -> dependency.getConfigTaskName())
          .forEach(configTaskName -> task.mustRunAfter(configTaskName));
    });
  }

  public <T extends Task> void configureTaskProjectModuleBuildDependencies(final String taskName,
      final Collection<CMakeResolvedProjectModuleDependency> projectModuleDependencies) {
    taskContainer.named(taskName).configure((task) -> {
      projectModuleDependencies.parallelStream()
          .filter(dependency -> dependency.isBuildable())
          .map(dependency -> dependency.getBuildTaskName())
          .filter(buildTaskName -> !task.getDependsOn().contains(buildTaskName))
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
