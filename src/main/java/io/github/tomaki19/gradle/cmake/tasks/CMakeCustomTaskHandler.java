/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeApplicationMatch;
import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeBuildConfigMatch;
import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeLibraryMatch;
import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeTestMatch;
import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeToolchainMatch;
import io.github.tomaki19.gradle.cmake.tasks.specs.CMakeToolchainTaskSpec;

public class CMakeCustomTaskHandler {

  private final Map<CMakeToolchainTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> customExecTaskProtos = new HashMap<>();
  private final Map<CMakeToolchainTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> customRuntimePackageTaskProtos = new HashMap<>();
  private final Map<CMakeToolchainTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> customDevelopPackageTaskProtos = new HashMap<>();

  private final TaskContainer taskContainer;

  public CMakeCustomTaskHandler(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public void registerCustomExec(final String name, final Class<CMakeCustomExec> type,
      final CMakeToolchainTaskSpec spec, Action<CMakeCustomExec> action) {
    customExecTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void registerCustomRuntimePackage(final String name, final Class<AbstractArchiveTask> type,
      final CMakeToolchainTaskSpec spec, Action<AbstractArchiveTask> action) {
    customRuntimePackageTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void registerCustomDevelopPackage(final String name, final Class<AbstractArchiveTask> type,
      final CMakeToolchainTaskSpec spec, Action<AbstractArchiveTask> action) {
    customDevelopPackageTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void applyCustomExec(final CMakeToolchainMatch match) {
    if (customExecTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<CMakeCustomExec> proto = customExecTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction());
    }
  }

  public void applyCustomExec(final CMakeBuildConfigMatch match) {
    if (customExecTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<CMakeCustomExec> proto = customExecTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction());
    }
  }

  public void applyCustomExec(final CMakeLibraryMatch match) {
    if (customExecTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<CMakeCustomExec> proto = customExecTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction());
    }
  }

  public void applyCustomRuntimePackage(final CMakeLibraryMatch match,
      final Action<AbstractArchiveTask> configureAction) {
    if (customRuntimePackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customRuntimePackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

  public void applyCustomDevelopPackage(final CMakeLibraryMatch match,
      final Action<AbstractArchiveTask> configureAction) {
    if (customDevelopPackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customDevelopPackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

  public void applyCustomExec(final CMakeApplicationMatch match) {
    if (customExecTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<CMakeCustomExec> proto = customExecTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction());
    }
  }

  public void applyCustomRuntimePackage(final CMakeApplicationMatch match,
      final Action<AbstractArchiveTask> configureAction) {
    if (customRuntimePackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customRuntimePackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

  public void applyCustomDevelopPackage(final CMakeApplicationMatch match,
      final Action<AbstractArchiveTask> configureAction) {
    if (customDevelopPackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customDevelopPackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

  public void applyCustomExec(final CMakeTestMatch match) {
    if (customExecTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<CMakeCustomExec> proto = customExecTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction());
    }
  }

  public void applyCustomRuntimePackage(final CMakeTestMatch match, final Action<AbstractArchiveTask> configureAction) {
    if (customRuntimePackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customRuntimePackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

  public void applyCustomDevelopPackage(final CMakeTestMatch match, final Action<AbstractArchiveTask> configureAction) {
    if (customDevelopPackageTaskProtos.containsKey(match)) {
      final CMakeCustomTaskProto<AbstractArchiveTask> proto = customDevelopPackageTaskProtos.get(match);
      taskContainer.register(proto.getName(), proto.getType(), proto.getAction()).configure(configureAction);
    }
  }

}
