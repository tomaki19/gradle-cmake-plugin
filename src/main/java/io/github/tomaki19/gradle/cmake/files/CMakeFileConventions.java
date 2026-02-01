/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CMakeFileConventions {

  public static final String CMAKE_CONFIG_PATH = "cmake/config";
  public static final String CMAKE_EXPORT_PATH = "cmake/export";
  public static final String CMAKE_INSTALL_PATH = "cmake/install";
  public static final Set<String> DEFAULT_BUILD_CONFIGS = new HashSet<>(Arrays.asList("debug", "release"));

  public static String cmakeConfigName(final String name, final String toolchainName) {
    return "%s-%s".formatted(name.toLowerCase(), toolchainName.toLowerCase());
  }

  public static String buildTarget(final String name, final String toolchainName,
      final String linkage, final String buildConfig) {
    return "%s-%s-%s-%s".formatted(name.toLowerCase(), toolchainName.toLowerCase(), linkage.toLowerCase(),
        buildConfig.toString().toLowerCase());
  }

  public static String buildTarget(final String name, final String toolchainName,
      final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchainName.toLowerCase(), buildConfig.toString().toLowerCase());
  }

  private CMakeFileConventions() {
  }
}
