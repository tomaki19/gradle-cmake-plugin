/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary<CMakeResolvedLibrary> {

  private final Collection<String> publicCompileDefinitions = new TreeSet<>();
  private final Collection<String> publicCompileOptions = new TreeSet<>();
  private final Collection<String> publicLinkOptions = new TreeSet<>();
  private final Collection<CMakeResolvedPackageDependency> publicPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectDependency> publicProjectDependencies = new TreeSet<>();

  CMakeResolvedLibrary(final CMakeLibrary library, final boolean stripDebug) {
    super(library, stripDebug);
  }

  public Collection<String> getPublicCompileDefinitions() {
    return Collections.unmodifiableCollection(publicCompileDefinitions);
  }

  void addPublicCompileDefinitions(final String definition) {
    publicCompileDefinitions.add(definition);
  }

  public Collection<String> getPublicCompileOptions() {
    return Collections.unmodifiableCollection(publicCompileOptions);
  }

  void addPublicCompileOptions(final String option) {
    publicCompileOptions.add(option);
  }

  public Collection<String> getPublicLinkOptions() {
    return Collections.unmodifiableCollection(publicLinkOptions);
  }

  public void addPublicLinkOption(final String option) {
    publicLinkOptions.add(option);
  }

  public Collection<CMakeResolvedPackageDependency> getPublicPackageDependencies() {
    return Collections.unmodifiableCollection(publicPackageDependencies);
  }

  public void addPublicPackageDependency(final CMakeResolvedPackageDependency dependency) {
    publicPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectDependency> getPublicProjectDependencies() {
    return Collections.unmodifiableCollection(publicProjectDependencies);
  }

  public void addPublicProjectDependency(final CMakeResolvedProjectDependency dependency) {
    publicProjectDependencies.add(dependency);
  }

}
