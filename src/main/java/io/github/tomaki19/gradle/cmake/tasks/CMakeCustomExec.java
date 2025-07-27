/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.Optional;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;

public abstract class CMakeCustomExec extends CMakeExec {

  protected final String compileCommands;

  @javax.inject.Inject
  public CMakeCustomExec(final CMakeToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull()), buildConfig);
    this.compileCommands = getProject().getLayout().getBuildDirectory().dir("%s/%s/%s/compile_commands.json"
        .formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName(), buildConfig))
        .get().getAsFile().getAbsolutePath();
  }

  public static String name(final String baseName, final CMakeToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s".formatted(baseName, toolchain.getName(), buildConfig);
  }

}
