/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakePackageExecutable extends CMakePackage {

  @Inject
  public CMakePackageExecutable(final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    super(CMakeFileConventions.buildTarget(binary.getName(), toolchain, buildConfig), toolchain);
  }

}
