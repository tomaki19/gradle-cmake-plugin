/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
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
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectPackageDependency;

public class CMakeTaskRegistry {

  private final TaskContainer taskContainer;

  private final Map<String, TaskProvider<? extends Task>> cmakeTaskMap = new HashMap<>();

  private enum GradleTasks {
    ASSEMBLE {
      public String toString() {
        return "assemble";
      }
    },
    BUILD {
      public String toString() {
        return "build";
      }
    },
    CHECK {
      public String toString() {
        return "check";
      }
    }
  }

  public CMakeTaskRegistry(final Project project) {
    this.taskContainer = project.getTasks();
  }

  public <T extends Task> TaskProvider<T> register(final String taskName, final Class<T> type,
      final Object... constructorArgs) throws InvalidUserDataException {
    final TaskProvider<T> task = taskContainer.register(taskName, type, constructorArgs);
    cmakeTaskMap.put(taskName, task);
    return task;
  }

  public TaskProvider<Task> register(final String taskName)
      throws InvalidUserDataException {
    final TaskProvider<Task> task = taskContainer.register(taskName);
    cmakeTaskMap.put(taskName, task);
    return task;
  }

  @SuppressWarnings("unchecked")
  public TaskProvider<Task> configure(final String taskName, final Action<? super Task> action) {
    return (TaskProvider<Task>) cmakeTaskMap.computeIfPresent(taskName, (key, value) -> {
      value.configure(action);
      return value;
    });
  }

  public void configureBuildTaskProjectModuleDependencies(final String taskName,
      final Collection<CMakeResolvedProjectPackageDependency> projectModuleDependencies) {
    taskContainer.named(taskName).configure((task) -> {
      projectModuleDependencies.stream()
          .filter(dependency -> !Objects.equals(dependency.getType(), CMakeLinkType.INTERFACE))
          .forEach(dependency -> dependency.getToolchain().getBuildConfigs()
              .forEach((buildConfig) -> task.dependsOn(CMakeTasksConventions.buildTaskName(dependency.getProject(),
                  dependency.getName(), dependency.getToolchain().getName(), dependency.getType(), buildConfig))));
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
