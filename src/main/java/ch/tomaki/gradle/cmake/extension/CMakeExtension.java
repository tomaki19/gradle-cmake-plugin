/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.NamedDomainObjectContainer;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final Map<String, List<String>> customTasks = new HashMap<>();

  public void register(final String taskName, final List<String> toolChainNames) {
    customTasks.put(taskName, toolChainNames);
  }

  public Map<String, List<String>> getCustomTasks() {
    return customTasks;
  }

  public abstract NamedDomainObjectContainer<CMakeFindPackage> getFindPackages();

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

}
