/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;

public abstract class CMakeTestExec extends CMakeExec {

  public final String buildTarget;

  @Inject
  public CMakeTestExec(final String buildTarget, final CMakeResolvedTest test) {
    super(test.getToolchain().getName(), test.getToolchain().getEnvironmentFile());
    this.buildTarget = buildTarget;
    setGroup(CMakeTasksConventions.GROUP_CHECK);
    setWorkingDir(getProject().getProjectDir());
    getBaseCommandLine().add("ctest");
    getBaseCommandLine().add("--tests-regex");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--test-dir");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, toolchainName))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--build-config");
    getBaseCommandLine().add(test.getBuildConfig());
  }
}
