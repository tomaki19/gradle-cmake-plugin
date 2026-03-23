/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final String projectName;
  private final TaskContainer projectTasks;
  private final CMakeLinkVariant linkType;

  CMakeResolvedProjectDependency(final String name, final Project project, final CMakeLinkVariant linkType) {
    super(name);
    this.projectName = project.getName();
    this.projectTasks = project.getTasks();
    this.linkType = linkType;
  }

  public String getProjectName() {
    return projectName;
  }

  public TaskProvider<CMakeBuildLibrary> getProjectTaskNamed(final String taskName) {
    return projectTasks.named(taskName, CMakeBuildLibrary.class);
  }

  public CMakeLinkVariant getLinkType() {
    return linkType;
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
    if ((comparator = getLinkType().compareTo(other.getLinkType())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
