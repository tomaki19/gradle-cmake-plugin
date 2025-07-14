/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeTestExec extends CMakeExec {

  protected final String buildName;

  @Inject
  public CMakeTestExec(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    this.buildName = buildTarget;
    setGroup(CMakeTasksConventions.GROUP_CHECK);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("ctest");
    getBaseCommandLine().add("--test-dir");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchainName))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--tests-regex");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--build-config");
    getBaseCommandLine().add(buildConfig);
  }
}
