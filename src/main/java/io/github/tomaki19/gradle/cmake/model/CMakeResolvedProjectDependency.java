/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ProjectDependency;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeLinkVariant linkType;
  private final String projectName;
  private final boolean remote;

  public CMakeResolvedProjectDependency(final String name, final CMakeLinkVariant linkType, final Project project,
      final boolean remote) {
    super(name);
    this.linkType = linkType;
    this.projectName = project.getName();
    this.remote = remote;
  }

  public String getProjectName() {
    return projectName;
  }

  public CMakeLinkVariant getLinkVariant() {
    return linkType;
  }

  public boolean isRemote() {
    return remote;
  }

  public boolean equals(final Project other) {
    if (other == null) {
      return false;
    }
    return Objects.equals(projectName, other.getName());
  }

  public ProjectDependency createProjectDependency(final Project project) {
    return project.getDependencyFactory().create(project.findProject(":%s".formatted(projectName)));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
    result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedProjectDependency other = (CMakeResolvedProjectDependency) obj;
    if (projectName == null) {
      if (other.projectName != null)
        return false;
    } else if (!projectName.equals(other.projectName))
      return false;
    if (linkType != other.linkType)
      return false;
    return true;
  }

  @Override
  public int compareTo(CMakeResolvedProjectDependency other) {
    int comparator = 0;
    if ((comparator = getProjectName().compareTo(other.getProjectName())) != 0) {
      return comparator;
    }
    if ((comparator = getName().compareTo(other.getName())) != 0) {
      return comparator;
    }
    if ((comparator = getLinkVariant().compareTo(other.getLinkVariant())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
