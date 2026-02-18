/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Optional;
import java.util.Objects;

import org.gradle.api.Project;

public final class CMakeResolvedProjectDependency
    extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeResolvedProject project;
  private final String linkage;

  CMakeResolvedProjectDependency(final Project project, final String name, final Optional<CMakeLinkType> linkage) {
    super(name);
    this.project = new CMakeResolvedProject(project);
    this.linkage = linkage.orElse(CMakeLinkType.SHARED).toString();
  }

  public CMakeResolvedProject getProject() {
    return project;
  }

  public String getLinkage() {
    return linkage;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((project == null) ? 0 : project.hashCode());
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
    if (project == null) {
      if (other.project != null)
        return false;
    } else if (!project.equals(other.project))
      return false;
    if (!Objects.equals(linkage, other.linkage))
      return false;
    return true;
  }

  @Override
  public int compareTo(CMakeResolvedProjectDependency other) {
    int comparator = 0;
    if ((comparator = getProject().compareTo(other.getProject())) != 0) {
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
