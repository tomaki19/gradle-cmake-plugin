/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolvedProjectPackageDependency
    extends CMakeResolvedName<CMakeResolvedProjectPackageDependency> {

  private final Project project;
  private final CMakeLinkType type;

  CMakeResolvedProjectPackageDependency(final Project project, final String name, final CMakeLinkType type) {
    super(name);
    this.project = project;
    this.type = type;
  }

  public Project getProject() {
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
    CMakeResolvedProjectPackageDependency other = (CMakeResolvedProjectPackageDependency) obj;
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
  public int compareTo(CMakeResolvedProjectPackageDependency other) {
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
