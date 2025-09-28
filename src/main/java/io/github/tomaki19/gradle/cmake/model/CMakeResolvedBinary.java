/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinary;

public abstract class CMakeResolvedBinary<T extends CMakeResolvedBinary<T>> extends CMakeResolvedName<T> {

  private final String outputName;
  private final Collection<String> headers;
  private final Collection<String> sources;
  private final Collection<String> privateCompileOptions;
  private final Collection<String> privateCompileDefinitions;
  private final Collection<String> privateLinkOptions = new TreeSet<>();
  private final Collection<String> privateSystemPackageDependencies = new TreeSet<>();
  private final Collection<CMakeResolvedProjectPackageDependency> privateProjectPackageDependencies = new TreeSet<>();
  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinary(final CMakeBinary binary, final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) throws IllegalArgumentException {
    super(binary.getName());
    this.outputName = binary.getOutputName().orElse(binary.getName());
    this.headers = new TreeSet<>(binary.getHeaders());
    this.sources = new TreeSet<>(binary.getSources());
    this.privateCompileOptions = new TreeSet<>(binary.getPrivateCompileOptions());
    this.privateCompileDefinitions = new TreeSet<>(binary.getPrivateCompileDefinitions());
    this.buildStatic = buildStatic || binary.getBuildStatic().orElse(Boolean.FALSE);
    this.buildShared = buildShared && binary.getBuildShared().orElse(Boolean.TRUE);
    this.stripDebug = stripDebug || binary.getStripDebug().orElse(Boolean.FALSE);
    this.packageBuildOutputs = packageBuildOutputs || binary.getPackageBuildOutputs().orElse(Boolean.FALSE);
  }

  public String getOutputName() {
    return outputName;
  }

  public Collection<String> getHeaders() {
    return headers;
  }

  public Collection<String> getSources() {
    return sources;
  }

  public Collection<String> getPrivateCompileOptions() {
    return privateCompileOptions;
  }

  public Collection<String> getPrivateCompileDefinitions() {
    return privateCompileDefinitions;
  }

  public Collection<String> getPrivateLinkOptions() {
    return privateLinkOptions;
  }

  void addPrivateLinkOption(final String option) {
    privateLinkOptions.add(option);
  }

  public Collection<String> getPrivateSystemPackageDependencies() {
    return privateSystemPackageDependencies;
  }

  void addPrivateSystemPackageDependency(final String dependency) {
    privateSystemPackageDependencies.add(dependency);
  }

  public Collection<CMakeResolvedProjectPackageDependency> getPrivateProjectPackageDependencies() {
    return privateProjectPackageDependencies;
  }

  void addPrivateProjectPackageDependency(final CMakeResolvedProjectPackageDependency dependency) {
    privateProjectPackageDependencies.add(dependency);
  }

  public boolean isBuildStatic() {
    return buildStatic;
  }

  public boolean isBuildShared() {
    return buildShared;
  }

  public boolean isStripDebug() {
    return stripDebug;
  }

  public boolean isPackageBuildOutputs() {
    return packageBuildOutputs;
  }

}
