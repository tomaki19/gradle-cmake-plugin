/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;

public class CMakeResolvedProjectModule {

  private final String name;
  private final Directory installDirectory;
  private final CMakeResolvedToolchain toolchain;

  CMakeResolvedProjectModule(final Project project, final CMakeResolvedToolchain toolchain) {
    this.name = project.getName();
    this.installDirectory = project.getLayout().getBuildDirectory().dir(CMakeListsConventions.CMAKE_INSTALL_PATH).get();
    this.toolchain = toolchain;
  }

  public String getName() {
    return name;
  }

  public Directory getInstallDirectory() {
    return installDirectory;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public String getAssembleConfigTaskName() {
    return ":%s:%s".formatted(name, CMakeTasksConventions.assembleConfigTaskName());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    return true;
  }

}
