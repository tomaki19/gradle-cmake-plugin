/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeBuild extends CMakeExec {

  protected final String buildTarget;

  @javax.inject.Inject
  CMakeBuild(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    setExecutable(OperatingSystem.current().getExecutableName("cmake"));
    args("--build");
    args(CMakeFileConventions.targetConfigDirectory(getProject().getLayout().getBuildDirectory(),
        toolchain, buildConfig).getAsFile().getAbsolutePath());
    args("--target");
    args(buildTarget);
    args("--config");
    args(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskContainer.GROUP_BUILD);
    getInputs().files(getProject().fileTree(getProject().getLayout().getProjectDirectory()));
    this.buildTarget = buildTarget;
  }

}
