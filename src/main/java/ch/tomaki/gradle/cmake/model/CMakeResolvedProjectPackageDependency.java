/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolvedProjectPackageDependency extends CMakeResolvedProjectPackage {

  private final CMakeLinkType type;
  private final String name;

  CMakeResolvedProjectPackageDependency(final String name, final CMakeResolvedToolchain toolchain,
      final CMakeLinkType type, final Project project) {
    super(toolchain, project);
    this.type = type;
    this.name = name;
  }

  public CMakeLinkType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedProjectPackageDependency other = (CMakeResolvedProjectPackageDependency) obj;
    if (type != other.type)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
