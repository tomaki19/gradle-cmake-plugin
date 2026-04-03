/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeBuildExecutable extends CMakeBuild {

  @javax.inject.Inject
  public CMakeBuildExecutable(final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(CMakeFileConventions.buildTarget(executable, toolchain, buildConfig), toolchain, buildConfig);
  }

}
