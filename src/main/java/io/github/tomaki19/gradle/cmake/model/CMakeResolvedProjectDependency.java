/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ProjectDependency;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApiException;

public final class CMakeResolvedProjectDependency extends CMakeResolvedName<CMakeResolvedProjectDependency> {

  private final CMakeLinkVariant linkVariant;
  private final String projectName;
  private final boolean remote;

  public CMakeResolvedProjectDependency(final String name, final CMakeLinkVariant linkVariant, final Project project,
      final boolean remote) {
    super(name);
    Objects.requireNonNull(linkVariant, "Link variant must not be null!");
    Objects.requireNonNull(project, "Project must not be null!");
    this.linkVariant = linkVariant;
    this.projectName = project.getName();
    this.remote = remote;
  }

  public String getProjectName() {
    return projectName;
  }

  public CMakeLinkVariant getLinkVariant() {
    return linkVariant;
  }

  public boolean isRemote() {
    return remote;
  }

  public boolean matchesProject(final Project other) {
    if (other == null) {
      return false;
    }
    return Objects.equals(projectName, other.getName());
  }

  public ProjectDependency createModulesDependency(final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Project targetProject = project.findProject(":%s".formatted(projectName));
    if (targetProject == null) {
      throw new CMakeApiException("Project '%s' not found in the build.".formatted(projectName));
    }
    final ProjectDependency projectDependency = project.getDependencyFactory().create(targetProject);
    projectDependency.setTargetConfiguration(CMakeConfigurationConventions
        .createModulesName(this, toolchain, buildConfig));
    return projectDependency;
  }

  public ProjectDependency createRuntimeDependency(final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Project targetProject = project.findProject(":%s".formatted(projectName));
    if (targetProject == null) {
      throw new CMakeApiException("Project '%s' not found in the build.".formatted(projectName));
    }
    final ProjectDependency projectDependency = project.getDependencyFactory().create(targetProject);
    projectDependency.setTargetConfiguration(CMakeConfigurationConventions
        .createRuntimeName(this, toolchain, buildConfig));
    return projectDependency;
  }

  public ProjectDependency createDevelopDependency(final Project project,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Project targetProject = project.findProject(":%s".formatted(projectName));
    if (targetProject == null) {
      throw new CMakeApiException("Project '%s' not found in the build.".formatted(projectName));
    }
    final ProjectDependency projectDependency = project.getDependencyFactory().create(targetProject);
    projectDependency.setTargetConfiguration(CMakeConfigurationConventions
        .createDevelopName(this, toolchain, buildConfig));
    return projectDependency;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + projectName.hashCode();
    result = prime * result + linkVariant.hashCode();
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
    if (!projectName.equals(other.projectName))
      return false;
    if (linkVariant != other.linkVariant)
      return false;
    return true;
  }

  @Override
  public int compareTo(CMakeResolvedProjectDependency other) {
    int result;
    if ((result = getProjectName().compareTo(other.getProjectName())) != 0) {
      return result;
    }
    if ((result = getName().compareTo(other.getName())) != 0) {
      return result;
    }
    return getLinkVariant().compareTo(other.getLinkVariant());
  }

}
