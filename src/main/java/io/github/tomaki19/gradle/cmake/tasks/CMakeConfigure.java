/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.file.Directory;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeConfigure extends CMakeExec {

  @javax.inject.Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile(), buildConfig);
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName(), buildConfig)).get();
    // tasks with same output directory are not run in parallel
    setWorkingDir(getProject().getProjectDir());
    getBaseCommand().set(OperatingSystem.current().getExecutableName("cmake"));
    getBaseArguments().add("-S %s".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    getBaseArguments().add("-B %s".formatted(outputDirectory.getAsFile().getAbsolutePath()));
    toolchain.getGenerator().ifPresent((generator) -> {
      getBaseArguments().add("-G \"%s\"".formatted(generator));
    });
    toolchain.getToolchainFile().ifPresent((toolchainFile) -> {
      getBaseArguments().add("--toolchain");
      getBaseArguments().add(" \"%s\"".formatted(toolchainFile.getAbsolutePath()));
    });
    getBaseArguments().add("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchain.getName()));
    getBaseArguments().add("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(buildConfig));
  }
}
