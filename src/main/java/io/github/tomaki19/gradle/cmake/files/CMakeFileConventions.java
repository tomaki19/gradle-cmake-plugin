/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeFileConventions {

  public static String CMAKE_CONFIG_PATH = "cmake/config";
  public static String CMAKE_EXPORT_PATH = "cmake/export";
  public static String CMAKE_INSTALL_PATH = "cmake/install";
  public static Set<String> DEFAULT_BUILD_CONFIGS = new HashSet<>(Arrays.asList("debug", "release"));

  public static String cmakeConfigName(final String name, final CMakeResolvedToolchain toolchain) {
    return "%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase());
  }

  public static String buildTarget(final String name, final CMakeResolvedToolchain toolchain,
      final String linkage, final String buildConfig) {
    return "%s-%s-%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase(), linkage.toLowerCase(),
        buildConfig.toString().toLowerCase());
  }

  public static String buildTarget(final String name, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toString().toLowerCase());
  }

  private CMakeFileConventions() {
  }
}
