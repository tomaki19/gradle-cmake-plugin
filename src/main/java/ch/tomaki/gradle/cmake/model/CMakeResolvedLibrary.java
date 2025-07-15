/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Collection;
import java.util.TreeSet;

import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary<CMakeResolvedLibrary> {

  private final Collection<String> publicCompileOptions;
  private final Collection<String> publicCompileDefinitions;
  private final Collection<String> publicLinkOptions = new TreeSet<>();
  private final Collection<String> publicSystemPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectPackageDependency> publicProjectPackageDependencies = new TreeSet<>();

  CMakeResolvedLibrary(final CMakeLibrary library, final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) {
    super(library, buildStatic, buildShared, stripDebug, packageBuildOutputs);
    this.publicCompileOptions = new TreeSet<>(library.getPublicCompileOptions().get());
    this.publicCompileDefinitions = new TreeSet<>(library.getPublicCompileDefinitions().get());
  }

  public Collection<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  public Collection<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  public Collection<String> getPublicLinkOptions() {
    return publicLinkOptions;
  }

  public void addPublicLinkOption(final String option) {
    publicLinkOptions.add(option);
  }

  public Collection<String> getPublicSystemPackageDependencies() {
    return publicSystemPackageDependencies;
  }

  public void addPublicSystemPackageDependency(final String dependency) {
    publicSystemPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectPackageDependency> getPublicProjectPackageDependencies() {
    return publicProjectPackageDependencies;
  }

  public void addPublicProjectPackageDependency(final CMakeResolvedProjectPackageDependency dependency) {
    publicProjectPackageDependencies.add(dependency);
  }

}
