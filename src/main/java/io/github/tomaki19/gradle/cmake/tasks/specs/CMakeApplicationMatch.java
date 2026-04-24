/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeApplicationMatch extends CMakeBuildConfigMatch {

  private final CMakeResolvedExecutable application;

  public CMakeApplicationMatch(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedExecutable application) {
    super(toolchain, buildConfig);
    this.application = application;
  }

  public CMakeResolvedExecutable getApplication() {
    return application;
  }

}
