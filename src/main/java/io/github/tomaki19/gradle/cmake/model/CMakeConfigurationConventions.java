/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public class CMakeConfigurationConventions {

  public static String createDirectoriesName(final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-directories".formatted(executable.getName(), toolchain.getName(),
        buildConfig);
  }

  public static String createDirectoriesName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-directories".formatted(library.getName(), library.getLinkVariant(), toolchain.getName(),
        buildConfig);
  }

  public static String createDirectoriesName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-directories".formatted(dependency.getName(), dependency.getLinkVariant(), toolchain.getName(),
        buildConfig);
  }

  private CMakeConfigurationConventions() {
  }
}
