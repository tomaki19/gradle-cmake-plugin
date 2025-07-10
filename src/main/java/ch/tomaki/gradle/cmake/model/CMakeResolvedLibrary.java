/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Set;

public interface CMakeResolvedLibrary {

  String getName();

  Set<String> getHeaders();

  Set<String> getPublicCompileOptions();

  Set<String> getPublicCompileDefinitions();

  Set<String> getPublicLinkOptions();

  Set<CMakeResolvedPackageDependency> getPublicPackageDependencies();

  Set<CMakeResolvedProjectDependency> getPublicProjectDependencies();

}
