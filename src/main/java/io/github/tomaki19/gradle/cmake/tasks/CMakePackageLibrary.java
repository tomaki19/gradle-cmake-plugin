/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakePackageLibrary extends CMakePackage {

  @javax.inject.Inject
  public CMakePackageLibrary(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String linkage, final String buildConfig) {
    super(CMakeFileConventions.buildTarget(binary.getName(), toolchain, linkage, buildConfig), toolchain, buildConfig);
  }

}
