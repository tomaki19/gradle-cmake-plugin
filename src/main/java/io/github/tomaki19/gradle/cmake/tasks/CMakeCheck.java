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
  public CMakeCheck(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    this.checkTarget = CMakeFileConventions.buildTarget(binary.getName(), toolchain.getName(), buildConfig);
    getBaseCommand().set(OperatingSystem.current().getExecutableName("ctest"));
    getBaseArguments().add("-T");
    getBaseArguments().add("Test");
    getBaseArguments().add("--test-dir");
    getBaseArguments().add(CMakeFileConventions.targetConfigDirectory(getProject().getLayout()
        .getBuildDirectory().get(), toolchain, buildConfig).getAsFile().getAbsolutePath());
    getBaseArguments().add("--tests-regex");
    getBaseArguments().add(checkTarget);
    getBaseArguments().add("--build-config");
    getBaseArguments().add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_CHECK);
  }
}
