/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions;
  private final Set<CMakeResolvedFindPackageDependency> publicFindPackageDependencies;
  private final Set<CMakeResolvedProjectModuleDependency> publicProjectModuleDependencies;

  public CMakeResolvedLibrary(final CMakeLibrary library, final Map<String, CMakeFindPackage> findPackages,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project) {
    super(library, findPackages, toolchain, buildConfig, project);
    this.publicCompileOptions = new HashSet<>(library.getPublicCompileOptions().get());
    this.publicCompileDefinitions = new HashSet<>(library.getPublicCompileDefinitions().get());
    this.publicLinkOptions = resolveLinkOptions(library.getPublicLinkDependencies().get());
    this.publicFindPackageDependencies = resolveFindPackageDependencies(
        library.getPublicLinkDependencies().get(), findPackages, toolchain);
    this.publicProjectModuleDependencies = resolveProjectModuleDependencies(
        library.getPublicLinkDependencies().get(), buildConfig, toolchain, project);
  }

  public final Set<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  public Set<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  public final Set<String> getPublicLinkOptions() {
    return publicLinkOptions;
  }

  public Set<CMakeResolvedFindPackageDependency> getPublicFindPackageDependencies() {
    return publicFindPackageDependencies;
  }

  public Set<CMakeResolvedProjectModuleDependency> getPublicProjectModuleDependencies() {
    return publicProjectModuleDependencies;
  }
}
