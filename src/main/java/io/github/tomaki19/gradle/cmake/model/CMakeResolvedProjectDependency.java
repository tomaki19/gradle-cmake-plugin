/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Optional;
import java.util.Objects;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeResolvedProject resolvedProject;
  private final String linkage;

  CMakeResolvedProjectDependency(final String name, final CMakeResolvedProject resolvedProject,
      final Optional<CMakeLinkType> linkage) {
    super(name);
    this.resolvedProject = resolvedProject;
    this.linkage = linkage.orElse(CMakeLinkType.SHARED).toString();
  }

  public CMakeResolvedProject getResolvedProject() {
    return resolvedProject;
  }

  public String getLinkage() {
    return linkage;
  }

  public String getProjectName() {
    return resolvedProject.getName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((resolvedProject == null) ? 0 : resolvedProject.hashCode());
    result = prime * result + ((linkage == null) ? 0 : linkage.hashCode());
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
    CMakeResolvedProjectDependency other = (CMakeResolvedProjectDependency) obj;
    if (resolvedProject == null) {
      if (other.resolvedProject != null)
        return false;
    } else if (!resolvedProject.equals(other.resolvedProject))
      return false;
    if (!Objects.equals(linkage, other.linkage))
      return false;
    return true;
  }

  @Override
  public int compareTo(CMakeResolvedProjectDependency other) {
    int comparator = 0;
    if ((comparator = getResolvedProject().compareTo(other.getResolvedProject())) != 0) {
      return comparator;
    }
    if ((comparator = getName().compareTo(other.getName())) != 0) {
      return comparator;
    }
    if ((comparator = getLinkage().compareTo(other.getLinkage())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
