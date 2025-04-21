/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public class CMakeResolvedProjectModuleDependency {

  private final Project project;
  private final CMakeResolvedToolchain toolchain;
  private final CMakeLinkType type;
  private final String buildTarget;

  CMakeResolvedProjectModuleDependency(final Project project, final CMakeResolvedToolchain toolchain,
      final CMakeLinkType type, final String buildTarget) {
    this.project = project;
    this.toolchain = toolchain;
    this.type = type;
    this.buildTarget = buildTarget;
  }

  public Project getProject() {
    return project;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public CMakeLinkType getType() {
    return type;
  }

  public String getBuildTarget() {
    return buildTarget;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((project == null) ? 0 : project.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
    result = prime * result + ((buildTarget == null) ? 0 : buildTarget.hashCode());
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
    if (project == null) {
      if (other.project != null)
        return false;
    } else if (!project.equals(other.project))
      return false;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    if (buildTarget == null) {
      if (other.buildTarget != null)
        return false;
    } else if (!buildTarget.equals(other.buildTarget))
      return false;
    return true;
  }

}
