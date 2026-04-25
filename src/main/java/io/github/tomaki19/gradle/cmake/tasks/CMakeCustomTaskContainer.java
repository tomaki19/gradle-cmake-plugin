/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gradle.api.Action;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskSpec;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeCustomTaskContainer {

  private final Map<CMakeCustomTaskSpec, CMakeCustomExecProto> customExecTaskProtos = new HashMap<>();
  private final Map<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> customRuntimePackageTaskProtos = new HashMap<>();
  private final Map<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> customDevelopPackageTaskProtos = new HashMap<>();

  private final TaskContainer taskContainer;

  public CMakeCustomTaskContainer(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public void registerExecTask(final Map<String, List<CharSequence>> entries, final String name,
      Action<CMakeCustomExec> action) {
    customExecTaskProtos.put(new CMakeCustomTaskSpec(entries), new CMakeCustomExecProto(name, action));
  }

  public <T extends AbstractArchiveTask> void registerRuntimePackageTask(final Map<String, List<CharSequence>> entries,
      final Class<T> type, final Action<AbstractArchiveTask> action) {
    customRuntimePackageTaskProtos.put(new CMakeCustomTaskSpec(entries), new CMakeCustomTaskProto<T>(type, action));
  }

  public <T extends AbstractArchiveTask> void registerDevelopPackageTask(final Map<String, List<CharSequence>> entries,
      final Class<T> type, final Action<AbstractArchiveTask> action) {
    customDevelopPackageTaskProtos.put(new CMakeCustomTaskSpec(entries), new CMakeCustomTaskProto<T>(type, action));
  }

  public void applyExecTask(final CMakeResolvedToolchain toolchain) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomExecProto> entry : customExecTaskProtos
        .entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain)
          && entry.getKey().hasNoBuildConfigs()
          && entry.getKey().hasNoComponents()) {
        taskContainer.register(entry.getValue().getName(), CMakeCustomExec.class, entry.getValue().getAction());
      }
    }
  }

  public void applyExecTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomExecProto> entry : customExecTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().hasNoComponents()) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getValue().getName(),
            CMakeCustomExec.class, entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomExecProto> entry : customExecTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getValue().getName(),
            CMakeCustomExec.class, entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimePackageTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final String taskName = CMakeTasksConventions.packageTaskName(library, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyDevelopPackageTask(final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final String taskName = CMakeTasksConventions.packageTaskName(library, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomExecProto> entry : customExecTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesApplication(application)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getValue().getName(),
            CMakeCustomExec.class, entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimePackageTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesApplication(application)) {
        final String taskName = CMakeTasksConventions.packageTaskName(application, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyDevelopPackageTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesApplication(application)) {
        final String taskName = CMakeTasksConventions.packageTaskName(application, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomExecProto> entry : customExecTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesTest(test)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getValue().getName(),
            CMakeCustomExec.class, entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimePackageTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customRuntimePackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesTest(test)) {
        final String taskName = CMakeTasksConventions.packageTaskName(test, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

  public void applyDevelopPackageTask(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeCustomTaskSpec, CMakeCustomTaskProto<? extends AbstractArchiveTask>> entry : customDevelopPackageTaskProtos
        .entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesTest(test)) {
        final String taskName = CMakeTasksConventions.packageTaskName(test, toolchain, buildConfig);
        final TaskProvider<? extends AbstractArchiveTask> provider = taskContainer.register(taskName,
            entry.getValue().getType(), entry.getValue().getAction());
        provider.configure(configureAction);
      }
    }
  }

}
