/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeTasksConventions {

  public static final String GROUP_BUILD = "cmake build";
  public static final String GROUP_CHECK = "cmake test";
  public static final String GROUP_PACKAGE = "cmake package";

  public static String assembleConfigTaskName() {
    return "assemble-cmake-config";
  }

  public static String assembleConfigTaskName(final Project project) {
    return ":%s:assemble-cmake-config".formatted(project.getName());
  }

  public static String assembleListsTaskName() {
    return "assemble-cmake-lists";
  }

  public static String customTaskName(final String name, final CMakeResolvedToolchain toolchain) {
    return "custom-%s-%s".formatted(name, toolchain.getName());
  }

  public static String configureTaskName(final CMakeResolvedToolchain toolchain) {
    return "configure-%s".formatted(toolchain.getName());
  }

  public static String configureTaskName(final Project project, final CMakeResolvedToolchain toolchain) {
    return ":%s:configure-%s".formatted(project.getName(), toolchain.getName());
  }

  public static String buildTaskName(final Project project, final String buildTarget) {
    return ":%s:build-%s".formatted(project.getName(), buildTarget);
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
