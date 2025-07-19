/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeBuild extends CMakeExec {

  protected final String buildTarget;

  @Input
  public abstract SetProperty<String> getAdditionalArguments();

   @Inject
 public CMakeBuild(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    setGroup(CMakeTaskRegistry.GROUP_BUILD);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("cmake");
    getBaseCommandLine().add("--build");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchainName))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--target");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--config");
    getBaseCommandLine().add(buildConfig);
    this.buildTarget = buildTarget;
  }

}
