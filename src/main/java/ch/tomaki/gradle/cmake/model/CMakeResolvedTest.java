
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extensions.CMakeTest;
import java.util.Map;
import org.gradle.api.Project;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  public CMakeResolvedTest(
      final CMakeTest test,
      final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Project project) {
    super(test, findPackages, toolchain, buildConfig, project);
  }
}
