/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import org.gradle.api.file.Directory;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeConfigure extends CMakeExec {

  @Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName())).get();
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
    getBaseCommandLine().add("-DCMAKE_CONFIGURATION_TYPES=\"%s\""
        .formatted(String.join(";", toolchain.getBuildConfigs())));
  }
}
