/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;

public final class CMakeTasksConventions {

  static String cleanListsTaskName() {
    return "clean-cmake-lists";
  }

  static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  static String assembleModuleTaskName(final String name, CMakeLinkVariant linkVariant,
      final String toolchainName, final String buildConfig) {
    return "assemble-%s-%s-%s-%s-module".formatted(name.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String assembleModuleTaskName(final String projectPath, final String name, CMakeLinkVariant linkVariant,
      final String toolchainName, final String buildConfig) {
    return "%s:assemble-%s-%s-%s-%s-module".formatted(projectPath, name.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String customExecTaskName(final String name, final CMakeToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s".formatted(name.toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  static String configureTaskName(final String projectPath, final String toolchainName, final String buildConfig) {
    return "%s:configure-%s-%s".formatted(projectPath, toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String configureTaskName(final String toolchainName, final String buildConfig) {
    return "configure-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String buildAllToolchainTaskName(final String toolchainName) {
    return "build-all-%s".formatted(toolchainName.toLowerCase());
  }

  static String buildAllBuildConfigTaskName(final String toolchainName, final String buildConfig) {
    return "build-all-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String buildTaskName(final String targetName, final CMakeLinkVariant linkVariant, final String toolchainName,
      final String buildConfig) {
    return "build-%s-%s-%s-%s".formatted(targetName.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String buildTaskName(final String targetName, final String toolchainName, final String buildConfig) {
    return "build-%s-%s-%s".formatted(targetName.toLowerCase(), toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String checkAllToolchainTaskName(final String toolchainName) {
    return "check-all-%s".formatted(toolchainName.toLowerCase());
  }

  static String checkAllBuildConfigTaskName(final String toolchainName, final String buildConfig) {
    return "check-all-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String checkTaskName(final String targetName, final CMakeLinkVariant linkVariant, final String toolchainName,
      final String buildConfig) {
    return "check-%s-%s-%s-%s".formatted(targetName.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String checkTaskName(final String targetName, final String toolchainName, final String buildConfig) {
    return "check-%s-%s-%s".formatted(targetName.toLowerCase(), toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String installAllToolchainTaskName(final String toolchainName) {
    return "install-all-%s".formatted(toolchainName.toLowerCase());
  }

  static String installAllBuildConfigTaskName(final String toolchainName, final String buildConfig) {
    return "install-all-%s-%s".formatted(toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String installTaskName(final String targetName, final CMakeLinkVariant linkVariant, final String toolchainName,
      final String buildConfig) {
    return "install-%s-%s-%s-%s".formatted(targetName.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String installTaskName(final String targetName, final String toolchainName, final String buildConfig) {
    return "install-%s-%s-%s".formatted(targetName.toLowerCase(), toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  static String packageTaskName(final String targetName, final CMakeLinkVariant linkVariant,
      final String toolchainName, final String buildConfig) {
    return "package-%s-%s-%s-%s".formatted(targetName.toLowerCase(), linkVariant.toLowerCase(),
        toolchainName.toLowerCase(), buildConfig.toLowerCase());
  }

  static String packageTaskName(final String targetName, final String toolchainName, final String buildConfig) {
    return "package-%s-%s-%s".formatted(targetName.toLowerCase(), toolchainName.toLowerCase(),
        buildConfig.toLowerCase());
  }

  private CMakeTasksConventions() {
  }
}
