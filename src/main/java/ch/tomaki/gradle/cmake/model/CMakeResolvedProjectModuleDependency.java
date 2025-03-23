
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.tasks.CMakeTasksConventions;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

public class CMakeResolvedProjectModuleDependency {

  private final String buildTarget;
  private final String projectName;
  private final String toolchainName;
  private final String buildTargetName;
  private final Directory projectDirectory;
  private final Directory installDirectory;
  private final boolean buildable;

  public CMakeResolvedProjectModuleDependency(
      final String buildTarget,
      final boolean buildable,
      final CMakeResolvedToolchain toolchain,
      final Project project) {
    this.buildTarget = "%s::%s".formatted(project.getName(), buildTarget);
    this.projectName = project.getName();
    this.toolchainName = toolchain.getName();
    this.buildTargetName = buildTarget;
    this.projectDirectory = project.getLayout().getProjectDirectory();
    this.installDirectory =
        project.getLayout().getBuildDirectory().dir(CMakeListsConventions.CMAKE_INSTALL_PATH).get();
    this.buildable = buildable;
  }

  public String getBuildTarget() {
    return buildTarget;
  }

  public String getProjectName() {
    return projectName;
  }

  public String getConfigTaskName() {
    return ":%s:%s"
        .formatted(projectName, CMakeTasksConventions.configureToolchainTaskName(toolchainName));
  }

  public String getBuildTaskName() {
    return ":%s:%s".formatted(projectName, CMakeTasksConventions.buildTaskName(buildTargetName));
  }

  public Directory getProjectDirectory() {
    return projectDirectory;
  }

  public Directory getInstallDirectory() {
    return installDirectory;
  }

  public boolean isBuildable() {
    return buildable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buildTarget == null) ? 0 : buildTarget.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CMakeResolvedProjectModuleDependency other = (CMakeResolvedProjectModuleDependency) obj;
    if (buildTarget == null) {
      if (other.buildTarget != null) return false;
    } else if (!buildTarget.equals(other.buildTarget)) return false;
    return true;
  }
}
