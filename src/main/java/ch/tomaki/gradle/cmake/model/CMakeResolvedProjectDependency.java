/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public class CMakeResolvedProjectDependency extends CMakeResolvedObject {

  private final Project project;
  private final CMakeResolvedToolchain toolchain;
  private final CMakeLinkType type;
  private final String buildConfig;

  CMakeResolvedProjectDependency(final Project project, final String name, final CMakeResolvedToolchain toolchain,
      final CMakeLinkType type, final String buildConfig) {
    super(name);
    this.project = project;
    this.toolchain = toolchain;
    this.type = type;
    this.buildConfig = buildConfig;
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

  public String getBuildConfig() {
    return buildConfig;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((project == null) ? 0 : project.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((buildConfig == null) ? 0 : buildConfig.hashCode());
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
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    if (type != other.type)
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
