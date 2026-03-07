/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.Optional;

import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.CacheableTask;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;

@CacheableTask
public abstract class CMakeCustomExec extends CMakeExec {

  protected final String compileCommands;

  @javax.inject.Inject
  public CMakeCustomExec(final String toolchainName, final String buildConfig, final Optional<RegularFile> environmentFile) {
    super(toolchainName, buildConfig, environmentFile);
    this.compileCommands = getProject().getLayout().getBuildDirectory().dir("%s/%s/%s/compile_commands.json"
        .formatted(CMakeFileConventions.CMAKE_CONFIG_PATH, toolchainName, buildConfig))
        .get().getAsFile().getAbsolutePath();
  }

}
