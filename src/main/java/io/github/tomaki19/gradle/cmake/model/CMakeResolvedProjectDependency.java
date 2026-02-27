/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Optional;
import java.util.Objects;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeResolvedProject resolvedProject;
  private final CMakeLinkType linkType;

  CMakeResolvedProjectDependency(final String name, final CMakeResolvedProject resolvedProject,
      final Optional<CMakeLinkType> linkage) {
    super(name);
    this.resolvedProject = resolvedProject;
    this.linkType = linkage.orElse(CMakeLinkType.SHARED);
  }

  public CMakeResolvedProject getResolvedProject() {
    return resolvedProject;
  }

  public CMakeLinkType getLinkType() {
    return linkType;
  }

  public String getProjectName() {
    return resolvedProject.getName();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((resolvedProject == null) ? 0 : resolvedProject.hashCode());
    result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
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
    if (!Objects.equals(linkType, other.linkType))
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
    if ((comparator = getLinkType().compareTo(other.getLinkType())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
