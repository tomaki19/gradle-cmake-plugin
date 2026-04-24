/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;

public class CMakeComponentTaskSpec extends CMakeBuildConfigTaskSpec {

  public static final String COMPONENTS = "*co*";
  public static final String LIBRARIES = "*li*";
  public static final String INTERFACES = "*in*";
  public static final String SHARED = "*sh*";
  public static final String STATIC = "*st*";
  public static final String APPLICATIONS = "*ap*";
  public static final String TESTS = "*te*";

  public final Set<String> components = new TreeSet<>();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((components == null) ? 0 : components.hashCode());
    return result;
  }

  public boolean equals(CMakeComponentTaskSpec other) {
    if (this == other)
      return true;
    if (!components.equals(other.components))
      return false;
    return true;
  }

  public boolean equals(final CMakeLibraryMatch match) {
    return super.equals(match)
        && ((!components.isEmpty() && (components.contains(match.getLibrary().getName())
            || components.contains(ALL) || components.contains(LIBRARIES)
            || (components.contains(INTERFACES)
                && Objects.equals(CMakeLinkVariant.INTERFACE, match.getLibrary().getLinkVariant()))
            || (components.contains(SHARED)
                && Objects.equals(CMakeLinkVariant.SHARED, match.getLibrary().getLinkVariant()))
            || (components.contains(STATIC)
                && Objects.equals(CMakeLinkVariant.STATIC, match.getLibrary().getLinkVariant())))));
  }

  public boolean equals(final CMakeApplicationMatch match) {
    return super.equals(match)
        && ((!components.isEmpty() && (components.contains(match.getApplication().getName())
            || components.contains(ALL) || components.contains(APPLICATIONS))));
  }

  public boolean equals(final CMakeTestMatch match) {
    return super.equals(match)
        && ((!components.isEmpty() && (components.contains(match.getTest().getName())
            || components.contains(ALL) || components.contains(TESTS))));
  }
}

/*
 *
 * cmake.tasks.register(
 * toolchains: ['gcc', 'vscp'],
 * buildConfigs: ['release', 'debug'],
 * componentTypes: [ALL, LIBRARIES, EXECUTABLES, APPLICATIONS, TESTS],
 * ) {
 * }
 *
 */
