
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

public final class CMakeResolvedFindPackageDependency {

  private final String name;
  private final String buildTarget;
  private final CMakeResolvedToolchain toolchain;

  public CMakeResolvedFindPackageDependency(
      final String name, final String buildTarget, final CMakeResolvedToolchain toolchain) {
    this.name = name;
    this.buildTarget = buildTarget;
    this.toolchain = toolchain;
  }

  public String getName() {
    return name;
  }

  public String getBuildTarget() {
    return buildTarget;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((buildTarget == null) ? 0 : buildTarget.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CMakeResolvedFindPackageDependency other = (CMakeResolvedFindPackageDependency) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (buildTarget == null) {
      if (other.buildTarget != null) return false;
    } else if (!buildTarget.equals(other.buildTarget)) return false;
    return true;
  }
}
