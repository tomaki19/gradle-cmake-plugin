/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.files.CMakeLinkType;
import ch.tomaki.gradle.cmake.files.CMakeListsConventions;

public class CMakeResolvedProjectModule {

  private final Project project;
  private final Optional<CMakeResolvedToolchain> toolchain;

  CMakeResolvedProjectModule(final Project project, final Optional<CMakeResolvedToolchain> toolchain) {
    this.project = project;
    this.toolchain = toolchain;
  }

  public Project getProject() {
    return project;
  }

  public Optional<CMakeResolvedToolchain> getToolchain() {
    return toolchain;
  }

  static void resolveProjectModuleDependencies(final Set<CMakeResolvedProjectModule> projectModules,
      final Set<CMakeResolvedProjectModuleDependency> projectModuleDependencies,
      final Optional<CMakeResolvedToolchain> toolchain, final Optional<String> buildConfig,
      final Set<String> dependencies, final Project project)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length == 3) {
          final Project dependencyProject = Objects.equals(dependencyTokens[0], project.getName()) ? project
              : project.findProject(":%s".formatted(dependencyTokens[0]));
          if (Objects.nonNull(dependencyProject)) {
            final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
            final String buildTarget = CMakeListsConventions.libraryTarget(dependencyTokens[1], type, toolchain,
                buildConfig);
            final CMakeResolvedProjectModuleDependency resolvedProjectModule = new CMakeResolvedProjectModuleDependency(
                dependencyProject, toolchain, type, buildTarget);
            projectModuleDependencies.add(resolvedProjectModule);
            if (!Objects.equals(project, dependencyProject)) {
              projectModules.add(new CMakeResolvedProjectModule(dependencyProject,
                  toolchain));
            }
          } else {
            throw new IllegalArgumentException(
                "Missing local project '%s'!".formatted(dependencyTokens[0]));
          }
        } else if (dependencyTokens.length > 3) {
          throw new IllegalArgumentException(
              "Invalid dependency '%s'!".formatted(dependency));
        }
      }
    }
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
