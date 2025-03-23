
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

  public static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  public static String configureToolchainTaskName(final String toolchainName) {
    return "configure-toolchain-%s".formatted(toolchainName);
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

  private CMakeTasksConventions() {}
}
