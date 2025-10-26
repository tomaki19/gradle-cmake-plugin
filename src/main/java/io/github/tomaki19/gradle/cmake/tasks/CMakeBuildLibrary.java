/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeBuildLibrary extends CMakeBuild {

  @javax.inject.Inject
  public CMakeBuildLibrary(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String linkage, final String buildConfig) {
    super(CMakeFileConventions.buildTarget(binary.getName(), toolchain, linkage, buildConfig), toolchain, buildConfig);
  }

}
