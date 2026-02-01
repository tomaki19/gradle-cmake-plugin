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
  private final Collection<String> publicSystemPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectDependency> publicProjectPackageDependencies = new TreeSet<>();

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

  public Collection<String> getPublicSystemPackageDependencies() {
    return Collections.unmodifiableCollection(publicSystemPackageDependencies);
  }

  public void addPublicSystemPackageDependency(final String dependency) {
    publicSystemPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectDependency> getPublicProjectPackageDependencies() {
    return Collections.unmodifiableCollection(publicProjectPackageDependencies);
  }

  public void addPublicProjectPackageDependency(final CMakeResolvedProjectDependency dependency) {
    publicProjectPackageDependencies.add(dependency);
  }

}
