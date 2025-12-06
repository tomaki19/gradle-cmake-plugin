/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary<CMakeResolvedLibrary> {

  private final Collection<String> publicCompileDefinitions = new TreeSet<>();
  private final Collection<String> publicCompileOptions = new TreeSet<>();
  private final Collection<String> publicLinkOptions = new TreeSet<>();
  private final Collection<String> publicPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectDependency> publicProjectDependencies = new TreeSet<>();

  CMakeResolvedLibrary(final CMakeLibrary library, final boolean stripDebug, final boolean packageBuildOutputs) {
    super(library, stripDebug, packageBuildOutputs);
  }

  public Collection<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  void addPublicCompileDefinitions(final String definition) {
    publicCompileDefinitions.add(definition);
  }

  public Collection<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  void addPublicCompileOptions(final String option) {
    publicCompileOptions.add(option);
  }

  public Collection<String> getPublicLinkOptions() {
    return publicLinkOptions;
  }

  public void addPublicLinkOption(final String option) {
    publicLinkOptions.add(option);
  }

  public Collection<String> getPublicPackageDependencies() {
    return publicPackageDependencies;
  }

  public void addPublicSystemPackageDependency(final String dependency) {
    publicPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectDependency> getPublicProjectDependencies() {
    return publicProjectDependencies;
  }

  public void addPublicProjectPackageDependency(final CMakeResolvedProjectDependency dependency) {
    publicProjectDependencies.add(dependency);
  }

}
