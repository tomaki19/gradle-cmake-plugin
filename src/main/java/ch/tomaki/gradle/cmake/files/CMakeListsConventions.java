/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CMakeListsConventions {

  public static String CMAKE_BUILD_PATH = "cmake/build";
  public static String CMAKE_INSTALL_PATH = "cmake/install";

  public static Set<String> DEFAULT_BUILD_CONFIGS = new HashSet<>(Arrays.asList("debug", "release"));

  public static String libraryInterfaceTarget(final String name) {
    return "%s-interface".formatted(name);
  }

  public static String libraryBinaryTarget(final String name, final CMakeLinkType type) {
    return "%s-%s".formatted(name, type.name().toLowerCase());
  }

  public static String applicationTarget(final String name) {
    return "%s".formatted(name);
  }

  public static String testTarget(final String name) {
    return "%s".formatted(name);
  }

  private CMakeListsConventions() {
  }
}
