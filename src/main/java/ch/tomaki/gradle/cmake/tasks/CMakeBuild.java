/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import org.gradle.api.provider.SetProperty;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

abstract class CMakeBuild extends CMakeExec {

  protected final String buildTarget;

  @javax.inject.Inject
  CMakeBuild(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    this.buildTarget = buildTarget;
    getBaseCommandLine().add("cmake");
    getBaseCommandLine().add("--build");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName()))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--target");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--config");
    getBaseCommandLine().add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_BUILD);
  }

  @org.gradle.api.tasks.Input
  protected abstract SetProperty<String> getAdditionalArguments();

}
