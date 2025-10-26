/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Optional;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

public abstract class CMakeBinaries {

  private Optional<Boolean> buildStatic = Optional.empty();
  private Optional<Boolean> buildShared = Optional.empty();
  private Optional<Boolean> stripDebug = Optional.empty();
  private Optional<Boolean> packageBuildOutputs = Optional.empty();

  @Nested
  public abstract CMakeCompile getPrivateCompile();

  public void privateCompile(Action<? super CMakeCompile> action) {
    action.execute(getPrivateCompile());
  }

  @Nested
  public abstract CMakeLinking getPrivateLinking();

  public void privateLinking(Action<? super CMakeLinking> action) {
    action.execute(getPrivateLinking());
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
