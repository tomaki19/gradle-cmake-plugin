/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

public class CMakeFileConventions {

  public static final String CMAKE_CONFIG_PATH = "cmake/config";
  public static final String CMAKE_EXPORT_PATH = "cmake/export";
  public static final String CMAKE_INSTALL_PATH = "cmake/install";

  public static String moduleTarget(final String projectName, final String name, final CMakeLinkType linkType,
      final String toolchainName, final String buildConfig) {
    return "%s-%s-%s-%s-%s-module".formatted(projectName, name.toLowerCase(), linkType.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final String projectName, final String name, final CMakeLinkType linkType,
      final String toolchainName, final String buildConfig) {
    return "%s::%s-%s-%s-%s".formatted(projectName, name.toLowerCase(), linkType.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final String name, final CMakeLinkType linkType, final String toolchainName,
      final String buildConfig) {
    return "%s-%s-%s-%s".formatted(name.toLowerCase(), linkType.toLowerCase(), toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String buildTarget(final String name, final String toolchainName, final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  private CMakeFileConventions() {
  }
}
