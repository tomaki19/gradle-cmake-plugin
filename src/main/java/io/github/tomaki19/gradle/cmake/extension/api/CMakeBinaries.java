/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CMakeBinaries {

  private Collection<String> privateLinkDependencies = new HashSet<>();
  private Optional<Boolean> buildStatic = Optional.empty();
  private Optional<Boolean> buildShared = Optional.empty();
  private Optional<Boolean> stripDebug = Optional.empty();
  private Optional<Boolean> packageBuildOutputs = Optional.empty();

  public Collection<String> getPrivateLinkDependencies() {
    return privateLinkDependencies;
  }

  public void setPrivateLinkDependencies(final Collection<CharSequence> values) {
    this.privateLinkDependencies = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

  public Optional<Boolean> getBuildStatic() {
    return buildStatic;
  }

  public void setBuildStatic(final Boolean value) {
    this.buildStatic = Optional.of(value);
  }

  public Optional<Boolean> getBuildShared() {
    return buildShared;
  }

  public void setBuildShared(final Boolean value) {
    this.buildShared = Optional.of(value);
  }

  public Optional<Boolean> getStripDebug() {
    return stripDebug;
  }

  public void setStripDebug(final Boolean value) {
    this.stripDebug = Optional.of(value);
  }

  public Optional<Boolean> getPackageBuildOutputs() {
    return packageBuildOutputs;
  }

  public void setPackageBuildOutputs(final Boolean value) {
    this.packageBuildOutputs = Optional.of(value);
  }
}
