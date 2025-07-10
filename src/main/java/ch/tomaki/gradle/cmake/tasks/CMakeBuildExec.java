/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeBuildExec extends CMakeExec {

  public final String buildName;

  @Input
  public abstract SetProperty<String> getAdditionalArguments();

  @Inject
  public CMakeBuildExec(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    this.buildName = "%s-%s".formatted(buildTarget, buildConfig);
    setGroup(CMakeTasksConventions.GROUP_BUILD);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("cmake");
    getBaseCommandLine().add("--build");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, toolchainName))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--target");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--config");
    getBaseCommandLine().add(buildConfig);
  }
}
