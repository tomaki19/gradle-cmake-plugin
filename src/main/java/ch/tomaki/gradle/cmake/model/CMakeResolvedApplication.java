/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedApplication extends CMakeResolvedBinary {

  CMakeResolvedApplication(final CMakeApplication application, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(application, toolchain, findPackages, project);
    addPrivateLinkDependencies(toolchain.getApplications().getPrivateLinkDependencies().get(), findPackages,
        project);
  }

}
