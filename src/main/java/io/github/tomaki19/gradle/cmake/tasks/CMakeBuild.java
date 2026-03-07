/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.provider.SetProperty;
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
    this.buildTarget = buildTarget;
    getBaseCommand().set(OperatingSystem.current().getExecutableName("cmake"));
    getBaseArguments().add("--build");
    getBaseArguments().add(getProject().getLayout().getBuildDirectory().get()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_CONFIG_PATH, toolchain.getName(), buildConfig))
        .getAsFile().getAbsolutePath());
    getBaseArguments().add("--target");
    getBaseArguments().add(buildTarget);
    getBaseArguments().add("--config");
    getBaseArguments().add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_BUILD);
    getInputs().files(getProject().fileTree(getProject().getLayout().getProjectDirectory()).getFiles());
  }

  @org.gradle.api.tasks.Input
  protected abstract SetProperty<String> getAdditionalArguments();

}
