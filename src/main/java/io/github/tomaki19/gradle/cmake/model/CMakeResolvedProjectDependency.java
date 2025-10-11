/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.Project;

import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolvedProjectDependency
    extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeResolvedProject project;
  private final CMakeLinkType type;

  CMakeResolvedProjectDependency(final Project project, final String name, final CMakeLinkType type) {
    super(name);
    this.project = new CMakeResolvedProject(project);
    this.type = type;
  }

  public CMakeResolvedProject getProject() {
    return project;
  }

  public CMakeLinkType getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((project == null) ? 0 : project.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    if (type != other.type)
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
    if ((comparator = getType().compareTo(other.getType())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
