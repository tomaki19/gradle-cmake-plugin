/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public class CMakeConfigurationConventions {

  public static String createModulesName(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-modules".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createModulesName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-modules".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String createModulesName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-modules".formatted(dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String createRuntimeName(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-runtime".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createRuntimeName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-runtime".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String createRuntimeName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-runtime".formatted(dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String createDevelopName(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-develop".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String createDevelopName(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-develop".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String createDevelopName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-develop".formatted(dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  private CMakeConfigurationConventions() {
  }
}
