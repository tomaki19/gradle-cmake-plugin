/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
import javax.inject.Inject;

public abstract class CMakeTestExec extends CMakeExec {

  public final String buildTarget;

  @Inject
  public CMakeTestExec(final String buildTarget, final CMakeResolvedTest test) {
    setGroup(CMakeTasksConventions.GROUP_CHECK);
    setWorkingDir(getProject().getProjectDir());
    this.buildTarget = buildTarget;
    test.getToolchain().getEnvironmentFile().ifPresent((file) -> {
      getEnvironmentFile().set(file);
    });
    getBaseCommandLine().add("ctest");
    getBaseCommandLine().add("--tests-regex");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--test-dir");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, test.getToolchain().getName()))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--build-config");
    getBaseCommandLine().add(test.getBuildConfig());
  }
}
