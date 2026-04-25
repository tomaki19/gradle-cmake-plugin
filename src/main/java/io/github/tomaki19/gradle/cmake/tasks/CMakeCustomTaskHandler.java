/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gradle.api.Action;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskSpec;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeCustomTaskHandler {

  private final Map<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> customExecTaskProtos = new HashMap<>();
  private final Map<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> customRuntimePackageTaskProtos = new HashMap<>();
  private final Map<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> customDevelopPackageTaskProtos = new HashMap<>();

  private final TaskContainer taskContainer;

  public CMakeCustomTaskHandler(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public void registerCustomExec(final String name, final Class<CMakeCustomExec> type,
      final CMakeCustomTaskSpec spec, Action<CMakeCustomExec> action) {
    customExecTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void registerCustomRuntimePackage(final String name, final Class<AbstractArchiveTask> type,
      final CMakeCustomTaskSpec spec, Action<AbstractArchiveTask> action) {
    customRuntimePackageTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void registerCustomDevelopPackage(final String name, final Class<AbstractArchiveTask> type,
      final CMakeCustomTaskSpec spec, Action<AbstractArchiveTask> action) {
    customDevelopPackageTaskProtos.put(spec, new CMakeCustomTaskProto<>(name, type, action));
  }

  public void applyCustomExec(final CMakeResolvedToolchain toolchain) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().hasNoBuildConfigs()
          && entry.getKey().hasNoComponents()) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction());
      }
    }
  }

  public void applyCustomExec(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig) && entry.getKey().hasNoComponents()) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction());
      }
    }
  }

  public void applyCustomExec(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig) && entry.getKey().matchesLibrary(library)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction());
      }
    }
  }

  public void applyCustomRuntimePackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig) && entry.getKey().matchesLibrary(library)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

  public void applyCustomDevelopPackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig) && entry.getKey().matchesLibrary(library)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

  public void applyCustomExec(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesApplication(application)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction());
      }
    }
  }

  public void applyCustomRuntimePackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesApplication(application)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

  public void applyCustomDevelopPackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesApplication(application)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

  public void applyCustomExec(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<CMakeCustomExec>> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesTest(test)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction());
      }
    }
  }

  public void applyCustomRuntimePackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesTest(test)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

  public void applyCustomDevelopPackage(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain) && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().matchesTest(test)) {
        taskContainer.register(entry.getValue().getName(), entry.getValue().getType(), entry.getValue().getAction()).configure(configureAction);
      }
    }
  }

}
