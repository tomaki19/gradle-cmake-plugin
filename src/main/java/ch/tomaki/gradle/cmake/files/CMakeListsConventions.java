/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.util.Optional;

import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeListsConventions {

  public static String CMAKE_BUILD_PATH = "cmake/build";
  public static String CMAKE_INSTALL_PATH = "cmake/install";

  public static String libraryTarget(final String name, final CMakeLinkType type,
      final Optional<CMakeResolvedToolchain> toolchain, final Optional<String> buildConfig) {
    switch (type) {
      case STATIC:
        return "library-%s-%s-static-%s".formatted(name, toolchain.get().getName(), buildConfig.get());
      case SHARED:
        return "library-%s-%s-shared-%s".formatted(name, toolchain.get().getName(), buildConfig.get());
      default:
        return libraryInterfaceTarget(name);
    }
  }

  public static String libraryInterfaceTarget(final String name) {
    return "library-%s-interface".formatted(name);
  }

  public static String applicationTarget(final String name, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "application-%s-%s-%s".formatted(name, toolchain.getName(), buildConfig);
  }

  public static String testTarget(final String name, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "test-%s-%s-%s".formatted(name, toolchain.getName(), buildConfig);
  }

  private CMakeListsConventions() {
  }
}
