/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

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

  public static String customTaskName(final String targetName, final String toolchainName) {
    return "custom-%s-%s".formatted(targetName, toolchainName);
  }

  public static String configureTaskName(final String toolchainName) {
    return "configure-%s".formatted(toolchainName);
  }

  public static String configureTaskName(final String projectName, final String toolchainName) {
    return ":%s:configure-%s".formatted(projectName, toolchainName);
  }

  public static String buildTaskName(final String projectName, final String buildTarget) {
    return ":%s:build-%s".formatted(projectName, buildTarget);
  }

  public static String buildTaskName(final String buildTarget) {
    return "build-%s".formatted(buildTarget);
  }

  public static String checkTaskName(final String buildTarget) {
    return "check-%s".formatted(buildTarget);
  }

  public static String packageTaskName(final String buildTarget) {
    return "package-%s".formatted(buildTarget);
  }

  private CMakeTasksConventions() {
  }
}
