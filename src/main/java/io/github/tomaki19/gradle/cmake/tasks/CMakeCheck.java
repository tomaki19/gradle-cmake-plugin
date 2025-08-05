/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeCheck extends CMakeExec {

  protected final String checkTarget;

  @javax.inject.Inject
  public CMakeCheck(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile(), buildConfig);
    this.checkTarget = CMakeFileConventions.buildTarget(binary.getName(), toolchain, buildConfig);
    getBaseCommand().set(OperatingSystem.current().getExecutableName("ctest"));
    baseArguments.add("-T");
    baseArguments.add("Test");
    baseArguments.add("--test-dir");
    baseArguments.add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName(), buildConfig))
        .get().getAsFile().getAbsolutePath());
    baseArguments.add("--tests-regex");
    baseArguments.add(checkTarget);
    baseArguments.add("--build-config");
    baseArguments.add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_CHECK);
  }
}
