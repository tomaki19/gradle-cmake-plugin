/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeTestMatch extends CMakeBuildConfigMatch {

  private final CMakeResolvedExecutable test;

  public CMakeTestMatch(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedExecutable test) {
    super(toolchain, buildConfig);
    this.test = test;
  }

  public CMakeResolvedExecutable getTest() {
    return test;
  }

}
