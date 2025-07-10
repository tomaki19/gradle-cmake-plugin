/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Set;

interface CMakeResolvedPrivatePackageDependencies {

  Set<CMakeResolvedPackage> getPrivatePackages();

  Set<CMakeResolvedPackageDependency> getPrivatePackageDependencies();

}
