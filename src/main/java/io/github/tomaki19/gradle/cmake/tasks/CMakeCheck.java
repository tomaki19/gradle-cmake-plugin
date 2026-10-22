/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.CMakeTaskContainer;
import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeCheck extends CMakeExec<CMakeCheck> {

  protected final String checkTarget;

  @javax.inject.Inject
  public CMakeCheck(final CMakeResolvedBinary<?> executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(CMakeCheck.class, toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    this.checkTarget = CMakeFileConventions.buildTarget(executable, toolchain, buildConfig);
    setExecutable(OperatingSystem.current().getExecutableName("ctest"));
    args("-T");
    args("Test");
    final File testDirectory = CMakeFileConventions.targetConfigDirectory(getProject().getLayout()
        .getBuildDirectory(), toolchain, buildConfig).getAsFile();
    args("--test-dir");
    args(testDirectory.getAbsolutePath());
    args("--tests-regex");
    args(checkTarget);
    args("--build-config");
    args(buildConfig);
    if (executable instanceof CMakeResolvedTest test && test.isTestResultsXmlOutput()) {
      args("--output-junit");
      args(new File(testDirectory, "%s-junit.xml".formatted(checkTarget)).getAbsolutePath());
    }
    setWorkingDir(getProject().getProjectDir());
    setGroup(CMakeTaskContainer.GROUP_CHECK);
  }
}
