/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Set;

public interface CMakeLibraryInterface {

  Set<String> getPublicLinkOptions();

  Set<CMakeResolvedFindPackageDependency> getPublicFindPackageDependencies();

  Set<CMakeResolvedProjectModuleDependency> getPublicProjectModuleDependencies();
}
