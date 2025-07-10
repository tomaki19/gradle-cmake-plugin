/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Set;

interface CMakeResolvedPublicProjectDependencies {

  Set<CMakeResolvedProject> getPublicProjectModules();

  Set<CMakeResolvedProjectDependency> getPublicProjectModuleDependencies();

}
