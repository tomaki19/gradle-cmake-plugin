/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public class CMakeConfigurationConventions {

  public static String createModulesDirectoriesName(final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-modules".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createModulesDirectoriesName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-modules".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createModulesDirectoriesName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-modules".formatted(dependency.getName().toLowerCase(), dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createOutputDirectoriesName(final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-outputs".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createOutputDirectoriesName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-outputs".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createOutputDirectoriesName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-outputs".formatted(dependency.getName().toLowerCase(), dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  private CMakeConfigurationConventions() {
  }
}
