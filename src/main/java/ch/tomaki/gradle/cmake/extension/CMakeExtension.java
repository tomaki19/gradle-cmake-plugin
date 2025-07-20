/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import java.util.Collection;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.tasks.TaskContainer;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.api.CMakeSystemPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;
import ch.tomaki.gradle.cmake.tasks.CMakeCustomExec;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final TaskContainer taskContainer;

  @javax.inject.Inject
  public CMakeExtension(final TaskContainer taskContainer) {
    this.taskContainer = taskContainer;
  }

  public abstract NamedDomainObjectContainer<CMakeSystemPackage> getPackages();

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakeCustomExec> configurationAction) {
    getToolchains().forEach((toolchain) -> {
      if (toolChainNames.contains(toolchain.getName())) {
        taskContainer.register(CMakeCustomExec.name(taskName, toolchain), CMakeCustomExec.class,
            toolchain).configure(configurationAction);
      }
    });
  }
}
