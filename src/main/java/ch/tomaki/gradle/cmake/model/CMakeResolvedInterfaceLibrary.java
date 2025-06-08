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

public final class CMakeResolvedInterfaceLibrary implements CMakeResolvedLibrary {

  private final String name;
  private final Set<String> headers;
  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions;
  private final Set<CMakeResolvedFindPackage> publicFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> publicFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> publicProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> publicProjectModuleDependencies;

  CMakeResolvedInterfaceLibrary(final CMakeLibrary library, final Map<String, CMakeFindPackage> findPackages,
      final Project project) throws IllegalArgumentException {
    this.name = library.getName();
    this.headers = new HashSet<>(library.getHeaders().get());
    this.publicCompileOptions = new HashSet<>(library.getPublicCompileOptions().get());
    this.publicCompileDefinitions = new HashSet<>(library.getPublicCompileDefinitions().get());
    this.publicLinkOptions = new HashSet<>();
    CMakeResolvedBinary.resolveLinkOptions(publicLinkOptions, library.getPublicLinkDependencies().get());
    this.publicFindPackages = new HashSet<>();
    this.publicFindPackageDependencies = new HashSet<>();
    CMakeResolvedFindPackage.resolveFindPackageDependencies(publicFindPackages, publicFindPackageDependencies,
        Optional.empty(), findPackages, library.getPublicLinkDependencies().get());
    this.publicProjectModules = new HashSet<>();
    this.publicProjectModuleDependencies = new HashSet<>();
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(publicProjectModules, publicProjectModuleDependencies,
        Optional.empty(), Optional.empty(), library.getPublicLinkDependencies().get(), project);
  }

  public String getName() {
    return name;
  }

  public Set<String> getHeaders() {
    return headers;
  }

  public Set<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  public Set<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  public Set<String> getPublicLinkOptions() {
    return publicLinkOptions;
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
