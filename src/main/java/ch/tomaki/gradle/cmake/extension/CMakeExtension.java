/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.NamedDomainObjectContainer;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final Map<String, String[]> customTasks = new HashMap<>();

  public void register(final String taskName, final String... toolChainNames) {
    customTasks.put(taskName, toolChainNames);
  }

  public Map<String, String[]> getCustomTasks() {
    return customTasks;
  }

  public abstract NamedDomainObjectContainer<CMakeFindPackage> getFindPackages();

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeBinary> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

}
