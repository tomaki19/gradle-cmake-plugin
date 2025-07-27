/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeConfigure extends CMakeExec {

  @javax.inject.Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile(), buildConfig);
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName(), buildConfig)).get();
    // tasks with same output directory are not run in parallel
    getOutputs().dir(outputDirectory);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("cmake");
    getBaseCommandLine().add("-S %s".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    getBaseCommandLine().add("-B %s".formatted(outputDirectory.getAsFile().getAbsolutePath()));
    getBaseCommandLine().add("-G \"%s\"".formatted(toolchain.getGenerator()));
    if (toolchain.getToolchainFile().isPresent()) {
      getBaseCommandLine().add("--toolchain");
      getBaseCommandLine().add(" \"%s\"".formatted(toolchain.getToolchainFile().get().getAbsolutePath()));
    }
    getBaseCommandLine().add("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchain.getName()));
    getBaseCommandLine().add("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(buildConfig));
  }
}
