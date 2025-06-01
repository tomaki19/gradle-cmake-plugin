/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeAbstractBinary;

public abstract class CMakeBuildExec extends CMakeExec {

  public final String buildTarget;

  @Input
  public abstract SetProperty<String> getAdditionalArguments();

  @Inject
  public CMakeBuildExec(final String buildTarget, final CMakeAbstractBinary binary) {
    super(binary.getToolchain().getName(), binary.getToolchain().getEnvironmentFile());
    this.buildTarget = buildTarget;
    setGroup(CMakeTasksConventions.GROUP_BUILD);
    setWorkingDir(getProject().getProjectDir());
    if (!binary.getSources().isEmpty()) {
      getBaseCommandLine().add("cmake");
      getBaseCommandLine().add("--build");
      getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
          .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, toolchainName))
          .get().getAsFile().getAbsolutePath());
      getBaseCommandLine().add("--target");
      getBaseCommandLine().add(buildTarget);
      getBaseCommandLine().add("--config");
      getBaseCommandLine().add(binary.getBuildConfig());
    }
  }
}
