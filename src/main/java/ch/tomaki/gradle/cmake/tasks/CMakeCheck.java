/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeCheck extends CMakeExec {

  protected final String checkTarget;

  @javax.inject.Inject
  public CMakeCheck(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(toolchain.getName(), toolchain.getEnvironmentFile());
    this.checkTarget = CMakeFileConventions.buildTarget(binary.getName(), toolchain, buildConfig);
    getBaseCommandLine().add("ctest");
    getBaseCommandLine().add("-T");
    getBaseCommandLine().add("Test");
    getBaseCommandLine().add("--test-dir");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName()))
        .get().getAsFile().getAbsolutePath());
    getBaseCommandLine().add("--tests-regex");
    getBaseCommandLine().add(checkTarget);
    getBaseCommandLine().add("--build-config");
    getBaseCommandLine().add(buildConfig);
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskRegistry.GROUP_CHECK);
  }
}
