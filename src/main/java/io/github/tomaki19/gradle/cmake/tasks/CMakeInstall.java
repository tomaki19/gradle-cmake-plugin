/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.nio.file.Paths;
import java.util.Optional;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeInstall extends CMakeExec {

  @javax.inject.Inject
  public CMakeInstall(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), buildConfig, Optional.empty());
    setGroup(CMakeTaskRegistry.GROUP_INSTALL);
    setWorkingDir(getProject().getLayout().getProjectDirectory());
    getBaseCommand().set(OperatingSystem.current().getExecutableName("cmake"));
    getBaseArguments().add("--install");
    getBaseArguments().add(Paths.get("build", CMakeFileConventions.CMAKE_CONFIG_PATH, toolchain.getName(),
        buildConfig).toString());
    getBaseArguments().add("--component");
    getBaseArguments().add(buildTarget);
    getBaseArguments().add("--prefix");
    getBaseArguments().add(Paths.get("build", CMakeFileConventions.CMAKE_INSTALL_PATH, buildTarget).toString());
  }

}
