/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeFileConventions {

  public static final String CMAKE_CONFIG_PATH = "cmake/config";
  public static final String CMAKE_INSTALL_PATH = "cmake/install";

  public static Directory targetConfigDirectory(final DirectoryProperty buildDirectory) {
    return buildDirectory.get().dir(CMAKE_CONFIG_PATH);
  }

  public static Directory targetConfigDirectory(final DirectoryProperty buildDirectory, final String toolchainName,
      final String buildConfig) {
    return targetConfigDirectory(buildDirectory).dir(toolchainName).dir(buildConfig);
  }

  public static Directory targetConfigDirectory(final DirectoryProperty buildDirectory,
      final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return targetConfigDirectory(buildDirectory).dir(toolchain.getName()).dir(buildConfig);
  }

  public static Directory targetBinaryDirectory(final DirectoryProperty buildDirectory,
      final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeFileConventions.buildTarget(executable, toolchain, buildConfig);
    return targetConfigDirectory(buildDirectory, toolchain, buildConfig).dir(target);
  }

  public static Directory targetBinaryDirectory(final DirectoryProperty buildDirectory,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final String target = CMakeFileConventions.buildTarget(library, toolchain, buildConfig);
    return targetConfigDirectory(buildDirectory, toolchain, buildConfig).dir(target);
  }

  public static Directory targetBinaryDirectory(final DirectoryProperty buildDirectory,
      final CMakeResolvedProjectDependency dependency, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final String target = CMakeFileConventions.buildTarget(dependency, toolchain, buildConfig);
    return targetConfigDirectory(buildDirectory, toolchain, buildConfig).dir(target);
  }

  private static String moduleTarget(final String projectName, final String libraryName,
      final String libraryLinkVariant, final String toolchainName, final String buildConfig) {
    return "%s-%s-%s-%s-%s-module".formatted(projectName, libraryName, libraryLinkVariant, toolchainName, buildConfig);
  }

  public static String moduleTarget(final Project project, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return moduleTarget(project.getName().toLowerCase(), library.getName().toLowerCase(),
        library.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String moduleTarget(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return moduleTarget(dependency.getProjectName().toLowerCase(), dependency.getName().toLowerCase(),
        dependency.getLinkVariant().toLowerCase(), toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "%s-%s-%s-%s".formatted(library.getName().toLowerCase(), library.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final CMakeResolvedProjectDependency dependency,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return "%s-%s-%s-%s".formatted(dependency.getName().toLowerCase(), dependency.getLinkVariant().toLowerCase(),
        toolchain.getName().toLowerCase(), buildConfig.toLowerCase());
  }

  public static String buildTarget(final CMakeResolvedExecutable executable, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    return "%s-%s-%s".formatted(executable.getName().toLowerCase(), toolchain.getName().toLowerCase(),
        buildConfig.toLowerCase());
  }

  private CMakeFileConventions() {
  }
}
