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
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions;
  private final Set<CMakeResolvedFindPackage> publicFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> publicFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> publicProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> publicProjectModuleDependencies;

  CMakeResolvedLibrary(final CMakeLibrary object, final CMakeToolchain toolchain,
      final String buildConfig, final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(object, toolchain, buildConfig, findPackages, project);
    this.publicCompileOptions = new HashSet<>(object.getPublicCompileOptions().get());
    this.publicCompileDefinitions = new HashSet<>(object.getPublicCompileDefinitions().get());
    this.publicLinkOptions = new HashSet<>();
    resolveLinkOptions(publicLinkOptions, object.getPublicLinkDependencies().get());
    this.publicFindPackages = new HashSet<>();
    this.publicFindPackageDependencies = new HashSet<>();
    resolveFindPackageDependencies(publicFindPackages, publicFindPackageDependencies, getResolvedToolchain(),
        findPackages, object.getPrivateLinkDependencies().get());
    this.publicProjectModules = new HashSet<>();
    this.publicProjectModuleDependencies = new HashSet<>();
    resolveProjectModuleDependencies(publicProjectModules, publicProjectModuleDependencies, getResolvedToolchain(),
        object.getPublicLinkDependencies().get(), buildConfig, project);
    addPrivateLinkDependencies(toolchain.getLibraryLinkDependencies().get(), findPackages, project);
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
