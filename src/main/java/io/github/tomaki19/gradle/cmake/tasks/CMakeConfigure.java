/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.stream.Collectors;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeConfigure extends CMakeExec {

  @javax.inject.Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    // tasks with same output directory are not run in parallel
    setWorkingDir(getProject().getProjectDir());
    // if gradle build file changes, configure needs to be run
    getInputs().file(getProject().getBuildFile());
    setExecutable(OperatingSystem.current().getExecutableName("cmake"));
    args("-S \"%s\"".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    args("-B \"%s\"".formatted(CMakeFileConventions
        .targetConfigDirectory(getProject().getLayout().getBuildDirectory(), toolchain, buildConfig)
        .getAsFile().getAbsolutePath()));
    args("-G \"%s\"".formatted(toolchain.getGenerator()));
    args("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchain.getName()));
    args("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(buildConfig));
    toolchain.getToolchainFile().ifPresent((toolchainFile) -> {
      args("--toolchain \"%s\"".formatted(toolchainFile.getAsFile().getAbsolutePath()));
    });
  }

  @Override
  protected void exec() {
    final String paths = getInputs().getFiles().getFiles().stream()
        .filter((file) -> file.isDirectory()).map((file) -> file.getAbsolutePath())
        .collect(Collectors.joining(";"));
    args("-DCMAKE_MODULE_PATH=\"%s\"".formatted(paths));
    super.exec();
  }
}
