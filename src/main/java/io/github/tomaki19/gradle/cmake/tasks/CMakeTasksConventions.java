/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTasksConventions {

  static String cleanListsTaskName() {
    return "clean-cmake-lists";
  }

  static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  static String customExecTaskName(final String name, final CMakeToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String assembleModuleTaskName(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "assemble-%s-%s-%s-%s-module".formatted(library.getName().toLowerCase(),
        library.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String assembleModuleTaskName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return ":%s:assemble-%s-%s-%s-%s-module".formatted(dependency.getProjectName(), dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String configureTaskName(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "configure-%s-%s".formatted(toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String configureTaskName(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return ":%s:configure-%s-%s".formatted(dependency.getProjectName(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String buildAllToolchainTaskName(final String toolchainName) {
    return "build-all-%s".formatted(toolchainName.toLowerCase());
  }

  static String buildAllBuildConfigTaskName(final String toolchainName, final String buildConfig) {
    return "build-all-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String buildTaskName(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "build-%s-%s-%s-%s".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String buildTaskName(final CMakeResolvedProjectDependency dependency, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return ":%s:build-%s-%s-%s-%s".formatted(dependency.getProjectName(), dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String buildTaskName(final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "build-%s-%s-%s".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String checkAllToolchainTaskName(final String toolchainName) {
    return "check-all-%s".formatted(toolchainName.toLowerCase());
  }

  static String checkAllBuildConfigTaskName(final String toolchainName, final String buildConfig) {
    return "check-all-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String checkTaskName(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "check-%s-%s-%s-%s".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String checkTaskName(final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "check-%s-%s-%s".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String packageTaskName(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "package-%s-%s-%s-%s".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String packageTaskName(final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "package-%s-%s-%s".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  public static String customPackageTaskName(final String name, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String customPackageTaskName(final String name, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s-%s".formatted(name.toLowerCase(), library.getName().toLowerCase(),
        library.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String customPackageTaskName(final String name, final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s".formatted(name.toLowerCase(), executable.getName().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  private CMakeTasksConventions() {
  }
}
