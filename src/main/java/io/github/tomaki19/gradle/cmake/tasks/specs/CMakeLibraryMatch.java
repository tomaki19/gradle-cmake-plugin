/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeLibraryMatch extends CMakeBuildConfigMatch {

  private final CMakeResolvedLibrary library;

  public CMakeLibraryMatch(final CMakeResolvedToolchain toolchain, final String buildConfig,
      final CMakeResolvedLibrary library) {
    super(toolchain, buildConfig);
    this.library = library;
  }

  public CMakeResolvedLibrary getLibrary() {
    return library;
  }

}
