/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gradle.api.Action;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApiException;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeArchiveTaskSpec;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecTaskSpec;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomZip;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTasksConventions;

public class CMakeCustomTaskContainer {

  private final Map<CMakeExecTaskSpec, Action<CMakeCustomExec>> customExecProtos = new HashMap<>();
  private final Map<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> customRuntimeArchiveProtos = new HashMap<>();
  private final Map<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> customDevelopArchiveProtos = new HashMap<>();

  private final TaskContainer taskContainer;

  public CMakeCustomTaskContainer(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public void registerExecTasks(final Map<String, Object> entries, Action<CMakeCustomExec> action)
      throws CMakeApiException {
    customExecProtos.put(CMakeExecTaskSpec.Init.create(entries), action);
  }

  public void registerExecTasks(final Map<String, Object> entries, final String prefix,
      final Action<CMakeCustomExec> action) throws CMakeApiException {
    customExecProtos.put(CMakeExecTaskSpec.Init.create(entries, prefix), action);
  }

  public <T extends AbstractArchiveTask> void registerRuntimeArchiveTasks(final Map<String, Object> entries,
      final Action<AbstractArchiveTask> action) throws CMakeApiException {
    customRuntimeArchiveProtos.put(CMakeArchiveTaskSpec.Init.create(entries, CMakeCustomZip.class), action);
  }

  public void registerRuntimeArchiveTasks(final Map<String, Object> entries) throws CMakeApiException {
    registerRuntimeArchiveTasks(entries, (task) -> {
    });
  }

  public void registerDevelopArchiveTasks(final Map<String, Object> entries, final Action<AbstractArchiveTask> action)
      throws CMakeApiException {
    customDevelopArchiveProtos.put(CMakeArchiveTaskSpec.Init.create(entries, CMakeCustomZip.class), action);
  }

  public <T extends AbstractArchiveTask> void registerDevelopArchiveTasks(final Map<String, Object> entries)
      throws CMakeApiException {
    registerDevelopArchiveTasks(entries, (task) -> {
    });
  }

  public void applyExecTasks(final CMakeResolvedToolchain toolchain) {
    for (final Entry<CMakeExecTaskSpec, Action<CMakeCustomExec>> entry : customExecProtos.entrySet()) {
      if (entry.getKey().matchesToolchain(toolchain)
          && entry.getKey().hasNoBuildConfigs()
          && entry.getKey().hasNoComponents()) {
        taskContainer.register(entry.getKey().getPrefix(), CMakeCustomExec.class,
            toolchain.getName(), "", toolchain.getEnvironmentFile())
            .configure(entry.getValue());
      }
    }
  }

  public void applyExecTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeExecTaskSpec, Action<CMakeCustomExec>> entry : customExecProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && entry.getKey().matchesBuildConfig(buildConfig)
          && entry.getKey().hasNoComponents()) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getKey().getPrefix(),
            CMakeCustomExec.class, toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
        provider.configure(entry.getValue());
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeExecTaskSpec, Action<CMakeCustomExec>> entry : customExecProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getKey().getPrefix(),
            CMakeCustomExec.class, toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
        provider.configure(entry.getValue());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimeArchiveTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> entry : customRuntimeArchiveProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final String taskName = CMakeTasksConventions.packageRuntimeTaskName(library, toolchain, buildConfig);
        final TaskProvider<AbstractArchiveTask> provider = registerArchiveTasks(taskName, entry);
        provider.configure(configureAction);
      }
    }
  }

  public void applyDevelopArchiveTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> entry : customDevelopArchiveProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesLibrary(library)) {
        final String taskName = CMakeTasksConventions.archiveDevelopTaskName(library, toolchain, buildConfig);
        final TaskProvider<AbstractArchiveTask> provider = registerArchiveTasks(taskName, entry);
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeExecTaskSpec, Action<CMakeCustomExec>> entry : customExecProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesApplication(application)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getKey().getPrefix(),
            CMakeCustomExec.class, toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
        provider.configure(entry.getValue());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimeArchiveTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedApplication application, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> entry : customRuntimeArchiveProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesApplication(application)) {
        final String taskName = CMakeTasksConventions.packageRuntimeTaskName(application, toolchain, buildConfig);
        final TaskProvider<AbstractArchiveTask> provider = registerArchiveTasks(taskName, entry);
        provider.configure(configureAction);
      }
    }
  }

  public void applyExecTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<CMakeCustomExec> configureAction) {
    for (final Entry<CMakeExecTaskSpec, Action<CMakeCustomExec>> entry : customExecProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesTest(test)) {
        final TaskProvider<CMakeCustomExec> provider = taskContainer.register(entry.getKey().getPrefix(),
            CMakeCustomExec.class, toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
        provider.configure(entry.getValue());
        provider.configure(configureAction);
      }
    }
  }

  public void applyRuntimeArchiveTasks(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedTest test, final Action<AbstractArchiveTask> configureAction) {
    for (final Entry<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> entry : customRuntimeArchiveProtos.entrySet()) {
      if ((entry.getKey().matchesToolchain(toolchain) || entry.getKey().hasNoToolchains())
          && (entry.getKey().matchesBuildConfig(buildConfig) || entry.getKey().hasNoBuildConfigs())
          && entry.getKey().matchesTest(test)) {
        final String taskName = CMakeTasksConventions.packageRuntimeTaskName(test, toolchain, buildConfig);
        final TaskProvider<AbstractArchiveTask> provider = registerArchiveTasks(taskName, entry);
        provider.configure(configureAction);
      }
    }
  }

  private TaskProvider<AbstractArchiveTask> registerArchiveTasks(final String taskName,
      final Entry<CMakeArchiveTaskSpec, Action<AbstractArchiveTask>> entry) {
    final Class<AbstractArchiveTask> type = (Class<AbstractArchiveTask>) entry.getKey().getType();
    final Action<AbstractArchiveTask> action = (Action<AbstractArchiveTask>) entry.getValue();
    return taskContainer.register("%s-%s".formatted(type.getSuperclass().getSimpleName().toLowerCase(), taskName), type,
        action);
  }

}
