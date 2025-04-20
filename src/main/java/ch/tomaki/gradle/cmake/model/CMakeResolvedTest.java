/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeTest;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  CMakeResolvedTest(final CMakeTest test, final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(test, toolchain, buildConfig, findPackages, project);
  }
}
