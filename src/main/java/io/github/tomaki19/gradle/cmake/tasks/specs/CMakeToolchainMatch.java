/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeToolchainMatch {

  final CMakeResolvedToolchain toolchain;

  public CMakeToolchainMatch(final CMakeResolvedToolchain toolchain) {
    this.toolchain = toolchain;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

}
