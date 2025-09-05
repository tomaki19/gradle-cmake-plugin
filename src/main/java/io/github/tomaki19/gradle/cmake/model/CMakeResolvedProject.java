/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

public class CMakeResolvedProject extends CMakeResolvedName<CMakeResolvedProject> {

  private final String identifier;
  private final Directory projectDirectory;
  private final Directory buildDirectory;

  public CMakeResolvedProject(final Project project) {
    super(project.getName());
    this.identifier = "%s:%s:%s".formatted(project.getGroup(), project.getName(), project.getVersion());
    this.projectDirectory = project.getLayout().getProjectDirectory();
    this.buildDirectory = project.getLayout().getBuildDirectory().get();
  }

  public String getIdentifier() {
    return identifier;
  }

  public Directory getProjectDirectory() {
    return projectDirectory;
  }

  public Directory getBuildDirectory() {
    return buildDirectory;
  }

}
