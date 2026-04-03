/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.dsl.DependencyFactory;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final Project project;
  private final CMakeLinkVariant linkType;
  private final boolean remote;

  CMakeResolvedProjectDependency(final String name, final Project project, final CMakeLinkVariant linkType,
      final boolean remote) {
    super(name);
    this.project = project;
    this.linkType = linkType;
    this.remote = remote;
  }

  public String getProjectName() {
    return project.getName();
  }

  public String getProjectPath() {
    return project.getPath();
  }

  public CMakeLinkVariant getLinkType() {
    return linkType;
  }

  public boolean isRemote() {
    return remote;
  }

  public boolean equals(final Project other) {
    if (other == null)
      return false;
    return Objects.equals(project.getName(), other.getName());
  }

  public ProjectDependency createProjectDependency(final DependencyFactory factory,
      final String targetConfiguration) {
    final ProjectDependency projectDependency = factory.create(project);
    // projectDependency.setTargetConfiguration(targetConfiguration);
    return projectDependency;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((project == null) ? 0 : project.getName().hashCode());
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
    if (project == null) {
      if (other.project != null)
        return false;
    } else if (!project.getName().equals(other.project.getName()))
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
    if ((comparator = getLinkType().compareTo(other.getLinkType())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
