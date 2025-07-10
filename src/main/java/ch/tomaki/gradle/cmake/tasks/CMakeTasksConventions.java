/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public final class CMakeTasksConventions {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_PACKAGE = "cmake package";

  public static String assembleConfigTaskName() {
    return "assemble-cmake-config";
  }

  public static String assembleConfigTaskName(final String projectName) {
    return ":%s:assemble-cmake-config".formatted(projectName);
  }

  public static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  public static String customTaskName(final String name, final String toolchainName) {
    return "custom-%s-%s".formatted(name, toolchainName);
  }

  public static String configureTaskName(final Project project, final String toolchainName) {
    return ":%s:configure-%s".formatted(project.getName(), toolchainName);
  }

  public static String configureTaskName(final String toolchainName) {
    return "configure-%s".formatted(toolchainName);
  }

  public static String buildTaskName(final Project project, final String targetName,
      final String toolchainName, final CMakeLinkType targetType, final String buildConfig) {
    return ":%s:build-%s-%s-%s-%s".formatted(project.getName(), targetName, toolchainName, targetType, buildConfig);
  }

  public static String buildTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "build-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  public static String buildTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "build-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  public static String buildAllTaskName(final String toolchainName) {
    return "build-all-%s".formatted(toolchainName);
  }

  public static String checkTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "check-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  public static String checkTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "check-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  public static String checkAllTaskName(final String toolchainName) {
    return "check-all-%s".formatted(toolchainName);
  }

  public static String packageTaskName(final String targetName, final String toolchainName,
      final CMakeLinkType targetType, final String buildConfig) {
    return "package-%s-%s-%s-%s".formatted(targetName, toolchainName, targetType, buildConfig);
  }

  public static String packageTaskName(final String targetName, final String toolchainName,
      final String buildConfig) {
    return "package-%s-%s-%s".formatted(targetName, toolchainName, buildConfig);
  }

  private CMakeTasksConventions() {
  }
}
