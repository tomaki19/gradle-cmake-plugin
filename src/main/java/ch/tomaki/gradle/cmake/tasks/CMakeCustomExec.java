/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.util.Optional;

import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public abstract class CMakeCustomExec extends CMakeExec {

  @javax.inject.Inject
  public CMakeCustomExec(final CMakeToolchain toolchain) {
    super(toolchain.getName(), Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull()));
  }

  public static String name(final String baseName, final CMakeToolchain toolchain) {
    return "%s-%s".formatted(baseName, toolchain.getName());
  }

}
