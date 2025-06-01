/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.util.Optional;

import javax.inject.Inject;

import org.gradle.api.file.Directory;

import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;

public abstract class CMakeConfigureExec extends CMakeExec {

  @Inject
  public CMakeConfigureExec(final CMakeToolchain toolchain) {
    super(toolchain.getName(), Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull()));
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, toolchain.getName())).get();
    // tasks with same output directory are not run in parallel
    getOutputs().dir(outputDirectory);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("cmake");
    getBaseCommandLine().add("-S %s".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    getBaseCommandLine().add("-B %s".formatted(outputDirectory.getAsFile().getAbsolutePath()));
    getBaseCommandLine().add("-G \"%s\"".formatted(toolchain.getGenerator().get()));
    if (toolchain.getToolchainFile().isPresent()) {
      getBaseCommandLine().add("--toolchain");
      getBaseCommandLine().add(" \"%s\"".formatted(toolchain.getToolchainFile().get().getAbsolutePath()));
    }
    getBaseCommandLine().add("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchainName));
    if (!toolchain.getBuildConfigs().get().isEmpty()) {
      getBaseCommandLine().add("-DCMAKE_CONFIGURATION_TYPES=\"%s\""
          .formatted(String.join(";", toolchain.getBuildConfigs().get())));
    }
  }
}
