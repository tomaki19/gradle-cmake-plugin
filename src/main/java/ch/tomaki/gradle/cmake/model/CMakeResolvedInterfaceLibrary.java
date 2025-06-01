/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedInterfaceLibrary extends CMakeAbstractInterface implements CMakeLibraryInterface {

  private final Set<CMakeResolvedFindPackage> publicFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> publicFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> publicProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> publicProjectModuleDependencies;

  CMakeResolvedInterfaceLibrary(final CMakeLibrary library, final Map<String, CMakeFindPackage> findPackages,
      final Project project) throws IllegalArgumentException {
    super(library);
    this.publicFindPackages = new HashSet<>();
    this.publicFindPackageDependencies = new HashSet<>();
    CMakeResolvedFindPackage.resolveFindPackageDependencies(publicFindPackages, publicFindPackageDependencies,
        Optional.empty(), findPackages, library.getPublicLinkDependencies().get());
    this.publicProjectModules = new HashSet<>();
    this.publicProjectModuleDependencies = new HashSet<>();
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(publicProjectModules, publicProjectModuleDependencies,
        Optional.empty(), Optional.empty(), library.getPublicLinkDependencies().get(), project);
  }

  public final Set<String> getPublicLinkOptions() {
    return new HashSet<>();
  }

  public Set<CMakeResolvedFindPackage> getPublicFindPackages() {
    return publicFindPackages;
  }

  public Set<CMakeResolvedFindPackageDependency> getPublicFindPackageDependencies() {
    return publicFindPackageDependencies;
  }

  public Set<CMakeResolvedProjectModule> getPublicProjectModules() {
    return publicProjectModules;
  }

  public Set<CMakeResolvedProjectModuleDependency> getPublicProjectModuleDependencies() {
    return publicProjectModuleDependencies;
  }

}
