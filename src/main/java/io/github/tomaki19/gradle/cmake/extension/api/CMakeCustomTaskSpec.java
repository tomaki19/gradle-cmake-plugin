/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeCustomTaskSpec {

  public static final String ALL = "*";
  public static final String COMPONENTS = "*co*";
  public static final String LIBRARIES = "*li*";
  public static final String INTERFACES = "*in*";
  public static final String SHARED = "*sh*";
  public static final String STATIC = "*st*";
  public static final String APPLICATIONS = "*ap*";
  public static final String TESTS = "*te*";

  public final Set<String> toolchains = new TreeSet<>();
  public final Set<String> buildConfigs = new TreeSet<>();
  public final Set<String> components = new TreeSet<>();

  public boolean hasNoToolchains() {
    return toolchains.isEmpty();
  }

  public boolean hasNoBuildConfigs() {
    return buildConfigs.isEmpty();
  }

  public boolean hasNoComponents() {
    return components.isEmpty();
  }

  public boolean matchesToolchain(final CMakeResolvedToolchain toolchain) {
    return toolchains.contains(toolchain.getName()) || toolchains.contains(ALL);
  }

  public boolean matchesBuildConfig(final String buildConfig) {
    return (buildConfigs.contains(buildConfig) || buildConfigs.contains(ALL));
  }

  public boolean matchesLibrary(final CMakeResolvedLibrary library) {
    return ((!components.isEmpty() && (components.contains(library.getName())
        || components.contains(ALL) || components.contains(LIBRARIES)
        || (components.contains(INTERFACES) && Objects.equals(CMakeLinkVariant.INTERFACE, library.getLinkVariant()))
        || (components.contains(SHARED) && Objects.equals(CMakeLinkVariant.SHARED, library.getLinkVariant()))
        || (components.contains(STATIC) && Objects.equals(CMakeLinkVariant.STATIC, library.getLinkVariant())))));
  }

  public boolean matchesApplication(final CMakeResolvedApplication application) {
    return ((!components.isEmpty() && (components.contains(application.getName())
        || components.contains(ALL) || components.contains(APPLICATIONS))));
  }

  public boolean matchesTest(final CMakeResolvedTest test) {
    return ((!components.isEmpty() && (components.contains(test.getName())
        || components.contains(ALL) || components.contains(TESTS))));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((toolchains == null) ? 0 : toolchains.hashCode());
    result = prime * result + ((buildConfigs == null) ? 0 : buildConfigs.hashCode());
    result = prime * result + ((components == null) ? 0 : components.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeCustomTaskSpec))
      return false;
    CMakeCustomTaskSpec other = (CMakeCustomTaskSpec) obj;
    if (toolchains == null) {
      if (other.toolchains != null)
        return false;
    } else if (!toolchains.equals(other.toolchains))
      return false;
    if (buildConfigs == null) {
      if (other.buildConfigs != null)
        return false;
    } else if (!buildConfigs.equals(other.buildConfigs))
      return false;
    if (components == null) {
      if (other.components != null)
        return false;
    } else if (!components.equals(other.components))
      return false;
    return true;
  }

}

/*
 * cmake.tasks.register(
 * toolchains: ['gcc', 'vscp'],
 * buildConfigs: ['release', 'debug'],
 * componentTypes: [ALL, LIBRARIES, EXECUTABLES, APPLICATIONS, TESTS],
 * ) {
 * ...
 * }
 */
