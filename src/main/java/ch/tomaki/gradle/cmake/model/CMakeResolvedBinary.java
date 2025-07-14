/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;

public abstract class CMakeResolvedBinary extends CMakeResolvedInterface {

  private final boolean buildStatic;
  private final boolean buildShared;
  private final boolean stripDebug;
  private final boolean packageBuildOutputs;

  CMakeResolvedBinary(final CMakeBinary binary, final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) throws IllegalArgumentException {
    super(binary);
    this.buildStatic = buildStatic || binary.getBuildStatic().getOrElse(Boolean.FALSE);
    this.buildShared = buildShared && binary.getBuildShared().getOrElse(Boolean.TRUE);
    this.stripDebug = stripDebug || binary.getStripDebug().getOrElse(Boolean.FALSE);
    this.packageBuildOutputs = packageBuildOutputs || binary.getPackageBuildOutputs().getOrElse(Boolean.FALSE);
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
