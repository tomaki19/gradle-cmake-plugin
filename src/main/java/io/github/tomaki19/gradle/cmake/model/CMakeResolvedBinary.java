/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinary;

public abstract class CMakeResolvedBinary<T extends CMakeResolvedBinary<T>> extends CMakeResolvedName<T> {

  private final String outputName;
  private final Collection<File> headers;
  private final Collection<File> sources;
  private final Collection<String> privateCompileDefinitions = new TreeSet<>();
  private final Collection<String> privateCompileOptions = new TreeSet<>();
  private final Collection<String> privateLinkOptions = new TreeSet<>();
  private final Collection<String> privateSystemPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectDependency> privateProjectPackageDependencies = new TreeSet<>();
  private final boolean stripDebug;

  CMakeResolvedBinary(final CMakeBinary binary, final boolean stripDebug) throws IllegalArgumentException {
    super(binary.getName());
    this.outputName = binary.getOutputName().getOrElse(binary.getName());
    this.headers = new TreeSet<>(binary.getHeaders().getSrcDirs());
    this.sources = new TreeSet<>(binary.getSources().getFiles());
    this.stripDebug = stripDebug || binary.getStripDebug().getOrElse(Boolean.FALSE);
  }

  public String getOutputName() {
    return outputName;
  }

  public Collection<File> getHeaders() {
    return Collections.unmodifiableCollection(headers);
  }

  public Collection<File> getSources() {
    return Collections.unmodifiableCollection(sources);
  }

  public Collection<String> getPrivateCompileDefinitions() {
    return Collections.unmodifiableCollection(privateCompileDefinitions);
  }

  void addPrivateCompileDefinitions(final String definition) {
    privateCompileDefinitions.add(definition);
  }

  public Collection<String> getPrivateCompileOptions() {
    return Collections.unmodifiableCollection(privateCompileOptions);
  }

  void addPrivateCompileOptions(final String option) {
    privateCompileOptions.add(option);
  }

  public Collection<String> getPrivateLinkOptions() {
    return Collections.unmodifiableCollection(privateLinkOptions);
  }

  void addPrivateLinkOption(final String option) {
    privateLinkOptions.add(option);
  }

  public Collection<String> getPrivateSystemPackageDependencies() {
    return Collections.unmodifiableCollection(privateSystemPackageDependencies);
  }

  void addPrivateSystemPackageDependency(final String dependency) {
    privateSystemPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectDependency> getPrivateProjectPackageDependencies() {
    return Collections.unmodifiableCollection(privateProjectPackageDependencies);
  }

  void addPrivateProjectPackageDependency(final CMakeResolvedProjectDependency dependency) {
    privateProjectPackageDependencies.add(dependency);
  }

  public boolean isStripDebug() {
    return stripDebug;
  }

}
