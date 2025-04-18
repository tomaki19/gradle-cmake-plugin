/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeObject;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  public CMakeResolvedApplication(
      final CMakeObject object,
      final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig,
      final Project project) {
    super(object, findPackages, toolchain, buildConfig, project);
  }
}
