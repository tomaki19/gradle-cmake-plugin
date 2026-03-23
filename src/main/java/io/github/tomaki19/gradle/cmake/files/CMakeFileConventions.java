/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeFileConventions {

  public static final String CMAKE_CONFIG_PATH = "cmake/config";
  public static final String CMAKE_INSTALL_PATH = "cmake/install";

  public static Directory targetConfigDirectory(final Directory buildDirectory, final CMakeToolchain toolchain,
      final String buildConfig) {
    return buildDirectory.dir(CMAKE_CONFIG_PATH).dir(toolchain.getName()).dir(buildConfig);
  }

  public static Directory targetConfigDirectory(final Directory buildDirectory, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return buildDirectory.dir(CMAKE_CONFIG_PATH).dir(toolchain.getName()).dir(buildConfig);
  }

  public static Directory targetBinaryDirectory(final Directory buildDirectory, final String target,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return targetConfigDirectory(buildDirectory, toolchain, buildConfig).dir(target);
  }

  public static String moduleTarget(final String projectName, final String name, final CMakeLinkVariant linkType,
      final String toolchainName, final String buildConfig) {
    return "%s-%s-%s-%s-%s-module".formatted(projectName, name.toLowerCase(), linkType.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final String name, final CMakeLinkVariant linkType, final String toolchainName,
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
