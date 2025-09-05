/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;


import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;

public final class CMakeTasksConventions {

  static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  static String assembleConfigTaskName(final String toolchainName) {
    return "assemble-%s-config".formatted(toolchainName);
  }

  static String assembleConfigTaskName(final String projectName, final String toolchainName) {
    return ":%s:assemble-%s-config".formatted(projectName, toolchainName);
  }

  static String configureTaskName(final String projectName, final String toolchainName, final String buildConfig) {
    return ":%s:configure-%s-%s".formatted(projectName, toolchainName, buildConfig);
  }

  static String configureTaskName(final String toolchainName, final String buildConfig) {
    return "configure-%s-%s".formatted(toolchainName, buildConfig);
  }

  static String buildTaskName(final String projectName, final String targetName,
      final String toolchainName, final CMakeLinkType targetType, final String buildConfig) {
    return ":%s:build-%s-%s-%s-%s".formatted(projectName, targetName, toolchainName, targetType, buildConfig);
  }

  static String buildTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "build-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  static String buildTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "build-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  static String buildAllTaskName(final String toolchainName) {
    return "build-all-%s".formatted(toolchainName);
  }

  static String checkTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "check-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  static String checkTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "check-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  static String checkAllTaskName(final String toolchainName) {
    return "check-all-%s".formatted(toolchainName);
  }

  static String packageTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "package-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  static String packageTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "package-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  private CMakeTasksConventions() {
  }
}
