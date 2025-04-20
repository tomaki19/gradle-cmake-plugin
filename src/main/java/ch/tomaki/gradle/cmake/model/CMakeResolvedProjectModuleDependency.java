/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;

public class CMakeResolvedProjectModuleDependency {

  private final String identifier;
  private final String projectName;
  private final String toolchainName;
  private final String buildTarget;
  private final boolean buildable;

  CMakeResolvedProjectModuleDependency(final String buildTarget, final boolean buildable,
      final CMakeResolvedToolchain toolchain, final Project project) {
    this.identifier = "%s::%s".formatted(project.getName(), buildTarget);
    this.projectName = project.getName();
    this.toolchainName = toolchain.getName();
    this.buildTarget = buildTarget;
    this.buildable = buildable;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getConfigTaskName() {
    return ":%s:%s".formatted(projectName, CMakeTasksConventions.configureTaskName(toolchainName));
  }

  public String getBuildTaskName() {
    return ":%s:%s".formatted(projectName, CMakeTasksConventions.buildTaskName(buildTarget));
  }

  public boolean isBuildable() {
    return buildable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedProjectModuleDependency other = (CMakeResolvedProjectModuleDependency) obj;
    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;
    return true;
  }
}
