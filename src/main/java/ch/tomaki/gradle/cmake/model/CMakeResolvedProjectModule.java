/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

public class CMakeResolvedProjectModule {

  private final Project project;
  private final CMakeResolvedToolchain toolchain;

  CMakeResolvedProjectModule(final Project project, final CMakeResolvedToolchain toolchain) {
    this.project = project;
    this.toolchain = toolchain;
  }

  public Project getProject() {
    return project;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((project == null) ? 0 : project.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
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
    CMakeResolvedProjectModule other = (CMakeResolvedProjectModule) obj;
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
    return true;
  }

}
