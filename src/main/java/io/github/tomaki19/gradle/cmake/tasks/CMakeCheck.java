/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeCheck extends CMakeExec {

  protected final String checkTarget;

  @javax.inject.Inject
  public CMakeCheck(final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    this.checkTarget = CMakeFileConventions.buildTarget(executable, toolchain, buildConfig);
    setExecutable(OperatingSystem.current().getExecutableName("ctest"));
    args("-T");
    args("Test");
    args("--test-dir");
    args(CMakeFileConventions.targetConfigDirectory(getProject().getLayout()
        .getBuildDirectory(), toolchain, buildConfig).getAsFile().getAbsolutePath());
    args("--tests-regex");
    args(checkTarget);
    args("--build-config");
    args(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_CHECK);
  }
}
