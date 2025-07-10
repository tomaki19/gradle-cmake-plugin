/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedInterfaceLibrary extends CMakeResolvedInterface implements CMakeResolvedLibrary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions = new HashSet<>();
  private final Set<CMakeResolvedPackage> publicPackages = new HashSet<>();
  private final Set<CMakeResolvedPackageDependency> publicPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProject> publicProjects = new HashSet<>();
  private final Set<CMakeResolvedProjectDependency> publicProjectDependencies = new HashSet<>();

  CMakeResolvedInterfaceLibrary(final CMakeLibrary library, CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) throws IllegalArgumentException {
    super(library, toolchain);
    this.publicCompileOptions = library.getPublicCompileOptions().get();
    this.publicCompileDefinitions = library.getPublicCompileDefinitions().get();
    CMakeResolvedBinary.resolveLinkOptions(library.getPublicLinkDependencies().get(), publicLinkOptions);
    CMakeResolvedPackage.resolvePackageDependencies(library.getPublicLinkDependencies().get(), publicPackages,
        publicPackageDependencies, getToolchain(), findPackages);
    CMakeResolvedProject.resolveProjectDependencies(library.getPublicLinkDependencies().get(), publicProjects,
        publicProjectDependencies, getToolchain(), project);
  }

  @Override
  public Set<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  @Override
  public Set<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  @Override
  public Set<String> getPublicLinkOptions() {
    return publicLinkOptions;
  }

  @Override
  public Set<CMakeResolvedPackageDependency> getPublicPackageDependencies() {
    return publicPackageDependencies;
  }

  @Override
  public Set<CMakeResolvedProjectDependency> getPublicProjectDependencies() {
    return publicProjectDependencies;
  }

}
