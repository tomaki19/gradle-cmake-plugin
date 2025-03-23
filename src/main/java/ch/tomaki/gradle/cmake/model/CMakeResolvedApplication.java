
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extensions.CMakeBinary;
import ch.tomaki.gradle.cmake.extensions.CMakeFindPackage;
import java.util.Map;
import org.gradle.api.Project;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  public CMakeResolvedApplication(
      final CMakeBinary binary,
      final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Project project) {
    super(binary, findPackages, toolchain, buildConfig, project);
  }
}
