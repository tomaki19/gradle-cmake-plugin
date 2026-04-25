/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.tasks.TaskContainer;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomTaskContainer;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final CMakeCustomTaskContainer customTaskHandler;

  @javax.inject.Inject
  public CMakeExtension(final TaskContainer taskContainer) {
    this.customTaskHandler = new CMakeCustomTaskContainer(taskContainer);
  }

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakePackage> getPackages();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

  public CMakeCustomTaskContainer getTasks() {
    return customTaskHandler;
  }

}
