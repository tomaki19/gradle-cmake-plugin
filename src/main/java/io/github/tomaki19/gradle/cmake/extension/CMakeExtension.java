/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import java.util.Collection;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.tasks.TaskContainer;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeSystemPackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final TaskContainer taskContainer;

  @javax.inject.Inject
  public CMakeExtension(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public abstract NamedDomainObjectContainer<CMakeSystemPackage> getSystemPackages();

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

  public void register(final String taskName, final Action<CMakeCustomExec> configurationAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs().get()) {
        taskContainer.register(CMakeCustomExec.name(taskName, toolchain, buildConfig), CMakeCustomExec.class,
            toolchain, buildConfig).configure(configurationAction);
      }
    }
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakeCustomExec> configurationAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs().get()) {
          taskContainer.register(CMakeCustomExec.name(taskName, toolchain, buildConfig), CMakeCustomExec.class,
              toolchain, buildConfig).configure(configurationAction);
        }
      }
    }
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakeCustomExec> configurationAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs().get()) {
          if (buildConfigs.contains(buildConfig)) {
            taskContainer.register(CMakeCustomExec.name(taskName, toolchain, buildConfig), CMakeCustomExec.class,
                toolchain, buildConfig).configure(configurationAction);
          }
        }
      }
    }
  }
}
