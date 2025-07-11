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

public final class CMakeResolvedLibrary extends CMakeResolvedBinary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions = new HashSet<>();
  private final Set<CMakeResolvedPackage> publicPackages = new HashSet<>();
  private final Set<CMakeResolvedPackageDependency> publicPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProject> publicProjects = new HashSet<>();
  private final Set<CMakeResolvedProjectDependency> publicProjectDependencies = new HashSet<>();
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedLibrary(final CMakeLibrary library, final CMakeToolchain toolchain,
      final Map<String, CMakeFindPackage> findPackages, final Project project) {
    super(library, toolchain, findPackages, project);
    this.publicCompileOptions = library.getPublicCompileOptions().get();
    this.publicCompileDefinitions = library.getPublicCompileDefinitions().get();
    CMakeResolvedBinary.resolveLinkOptions(library.getPublicLinkDependencies().get(), publicLinkOptions);
    CMakeResolvedPackage.resolvePackageDependencies(library.getPublicLinkDependencies().get(), publicPackages,
        publicPackageDependencies, getToolchain(), findPackages);
    CMakeResolvedProject.resolveProjectDependencies(library.getPublicLinkDependencies().get(), publicProjects,
        publicProjectDependencies, getToolchain(), project);
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

  public Set<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  public Set<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  public Set<String> getPublicLinkOptions() {
    return publicLinkOptions;
  }

  public Set<CMakeResolvedPackage> getPublicPackages() {
    return publicPackages;
  }

  public Set<CMakeResolvedPackageDependency> getPublicPackageDependencies() {
    return publicPackageDependencies;
  }

  public Set<CMakeResolvedProject> getPublicProjects() {
    return publicProjects;
  }

  public Set<CMakeResolvedProjectDependency> getPublicProjectDependencies() {
    return publicProjectDependencies;
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
