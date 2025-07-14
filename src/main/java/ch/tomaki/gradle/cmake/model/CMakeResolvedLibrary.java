/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary {

  private final Set<String> publicCompileOptions;
  private final Set<String> publicCompileDefinitions;
  private final Set<String> publicLinkOptions = new HashSet<>();
  private final Set<String> publicSystemPackageDependencies = new HashSet<>();
  private final Set<CMakeResolvedProjectPackageDependency> publicProjectPackageDependencies = new HashSet<>();

  CMakeResolvedLibrary(final CMakeLibrary library, final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) {
    super(library, buildStatic, buildShared, stripDebug, packageBuildOutputs);
    this.publicCompileOptions = library.getPublicCompileOptions().get();
    this.publicCompileDefinitions = library.getPublicCompileDefinitions().get();
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

  public void addPublicLinkOption(final String option) {
    publicLinkOptions.add(option);
  }

  public Set<String> getPublicSystemPackageDependencies() {
    return publicSystemPackageDependencies;
  }

  public void addPublicSystemPackageDependency(final String dependency) {
    publicSystemPackageDependencies.add(dependency);
  }

  public Set<CMakeResolvedProjectPackageDependency> getPublicProjectPackageDependencies() {
    return publicProjectPackageDependencies;
  }

  public void addPublicProjectPackageDependency(final CMakeResolvedProjectPackageDependency dependency) {
    publicProjectPackageDependencies.add(dependency);
  }

}
