/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.Project;

import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;

public final class CMakeTasksConventions {

  static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  static String assembleConfigTaskName(final String toolchainName) {
    return "assemble-%s-config".formatted(toolchainName);
  }

  static String assembleConfigTaskName(final Project project, final String toolchainName) {
    return ":%s:assemble-%s-config".formatted(project.getName(), toolchainName);
  }

  static String configureTaskName(final Project project, final String toolchainName) {
    return ":%s:configure-%s".formatted(project.getName(), toolchainName);
  }

  static String configureTaskName(final String toolchainName) {
    return "configure-%s".formatted(toolchainName);
  }

  static String buildTaskName(final Project project, final String targetName,
      final String toolchainName, final CMakeLinkType targetType, final String buildConfig) {
    return ":%s:build-%s-%s-%s-%s".formatted(project.getName(), targetName, toolchainName, targetType, buildConfig);
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
