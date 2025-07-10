/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedTest extends CMakeResolvedBinary {

  CMakeResolvedTest(final CMakeTest test, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(test, toolchain, findPackages, project);
    addPrivateLinkDependencies(toolchain.getTests().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

}
