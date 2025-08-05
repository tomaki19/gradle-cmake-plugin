/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.provider.SetProperty;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

abstract class CMakeBuild extends CMakeExec {

  protected final String buildTarget;

  @javax.inject.Inject
  CMakeBuild(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile(), buildConfig);
    this.buildTarget = buildTarget;
    getBaseCommand().set(OperatingSystem.current().getExecutableName("cmake"));
    baseArguments.add("--build");
    baseArguments.add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName(), buildConfig))
        .get().getAsFile().getAbsolutePath());
    baseArguments.add("--target");
    baseArguments.add(buildTarget);
    baseArguments.add("--config");
    baseArguments.add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_BUILD);
  }

  @org.gradle.api.tasks.Input
  protected abstract SetProperty<String> getAdditionalArguments();

}
