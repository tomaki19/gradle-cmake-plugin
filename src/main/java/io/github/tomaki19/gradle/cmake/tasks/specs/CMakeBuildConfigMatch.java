/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeBuildConfigMatch extends CMakeToolchainMatch {

  private final String buildConfig;

  public CMakeBuildConfigMatch(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain);
    this.buildConfig = buildConfig;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

}
