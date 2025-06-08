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
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedBinaryLibrary extends CMakeResolvedBinary implements CMakeResolvedLibrary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions;
  private final Set<CMakeResolvedFindPackage> publicFindPackages;
  private final Set<CMakeResolvedFindPackageDependency> publicFindPackageDependencies;
  private final Set<CMakeResolvedProjectModule> publicProjectModules;
  private final Set<CMakeResolvedProjectModuleDependency> publicProjectModuleDependencies;
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinaryLibrary(final CMakeLibrary library, final CMakeToolchain toolchain,
      final String buildConfig, final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(library, toolchain, buildConfig, findPackages, project);
    this.publicCompileOptions = new HashSet<>(library.getPublicCompileOptions().get());
    this.publicCompileDefinitions = new HashSet<>(library.getPublicCompileDefinitions().get());
    this.publicLinkOptions = new HashSet<>();
    CMakeResolvedBinary.resolveLinkOptions(getPrivateLinkOptions(), library.getPrivateLinkDependencies().get());
    CMakeResolvedBinary.resolveLinkOptions(publicLinkOptions, library.getPublicLinkDependencies().get());
    this.publicFindPackages = new HashSet<>();
    this.publicFindPackageDependencies = new HashSet<>();
    CMakeResolvedFindPackage.resolveFindPackageDependencies(getPrivateFindPackages(),
        getPrivateFindPackageDependencies(), Optional.of(getToolchain()), findPackages,
        library.getPrivateLinkDependencies().get());
    CMakeResolvedFindPackage.resolveFindPackageDependencies(publicFindPackages, publicFindPackageDependencies,
        Optional.of(getToolchain()), findPackages, library.getPublicLinkDependencies().get());
    this.publicProjectModules = new HashSet<>();
    this.publicProjectModuleDependencies = new HashSet<>();
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(getPrivateProjectModules(),
        getPrivateProjectModuleDependencies(), Optional.of(getToolchain()), Optional.of(buildConfig),
        library.getPrivateLinkDependencies().get(), project);
    CMakeResolvedProjectModule.resolveProjectModuleDependencies(publicProjectModules, publicProjectModuleDependencies,
        Optional.of(getToolchain()), Optional.of(buildConfig), library.getPublicLinkDependencies().get(), project);
    this.buildStatic = library.getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getLibraries().getBuildStatic().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = library.getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getLibraries().getBuildShared().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = library.getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = library.getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getLibraries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
        || toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE);
    addPrivateLinkDependencies(toolchain.getLibraries().getPrivateLinkDependencies().get(), findPackages,
        project);
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

  public Set<CMakeResolvedFindPackage> getPublicFindPackages() {
    return publicFindPackages;
  }

  @Override
  public Set<CMakeResolvedFindPackageDependency> getPublicFindPackageDependencies() {
    return publicFindPackageDependencies;
  }

  public Set<CMakeResolvedProjectModule> getPublicProjectModules() {
    return publicProjectModules;
  }

  @Override
  public Set<CMakeResolvedProjectModuleDependency> getPublicProjectModuleDependencies() {
    return publicProjectModuleDependencies;
  }

  @Override
  public boolean isBuildStatic() {
    return buildStatic;
  }

  @Override
  public boolean isBuildShared() {
    return buildShared;
  }

  @Override
  public boolean isStripDebug() {
    return stripDebug;
  }

  @Override
  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

}
