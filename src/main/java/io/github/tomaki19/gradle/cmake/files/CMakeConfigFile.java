/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeConfigFile extends CMakeFileOutputStream {

  private final CMakeResolvedToolchain toolchain;
  private final Project project;

  public CMakeConfigFile(final Project project, final CMakeResolvedToolchain toolchain) throws FileNotFoundException {
    super(project.getLayout().getBuildDirectory().dir(CMakeFileConventions.CMAKE_BUILD_PATH).get()
        .file("%s-config.cmake".formatted(CMakeFileConventions.cmakeConfigName(project, toolchain))));
    this.toolchain = toolchain;
    this.project = project;
  }

  @Override
  public void write() throws IOException {
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
        if (library.getSources().isEmpty()) {
          final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
              CMakeLinkType.INTERFACE, buildConfig);
          writeLine();
          write("add_library( %s::%s INTERFACE IMPORTED )", project.getName(), libraryTarget);
          setTargetProperties(library, libraryTarget, project);
        } else {
          if (library.isBuildStatic()) {
            final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
                CMakeLinkType.STATIC, buildConfig);
            final String outputName = toolchain.getOperatingSystem().getStaticLibraryName(library.getOutputName());
            writeLine();
            write("add_library( %s::%s STATIC IMPORTED )", project.getName(), libraryTarget);
            setTargetProperties(library, libraryTarget, outputName, toolchain, buildConfig, project);
          }
          if (library.isBuildShared()) {
            final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
                CMakeLinkType.SHARED, buildConfig);
            final String outputName = toolchain.getOperatingSystem().getSharedLibraryName(library.getOutputName());
            writeLine();
            write("add_library( %s::%s SHARED IMPORTED )", project.getName(), libraryTarget);
            setTargetProperties(library, libraryTarget, outputName, toolchain, buildConfig, project);
          }
        }
      }
    }
  }

  private void setTargetProperties(final CMakeResolvedLibrary library, final String objectTarget,
      final Project project) throws IOException {
    write("set_target_properties( %s::%s PROPERTIES", project.getName(), objectTarget);
    write(1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getHeaders()) {
      final File includeDir = project.getLayout().getProjectDirectory().dir(include).getAsFile();
      write(2, "%s", includeDir.toURI().getPath());
    }
    write(")");
  }

  private void setTargetProperties(final CMakeResolvedLibrary library, final String libraryTarget,
      final String outputName, final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project)
      throws IOException {
    write("set_target_properties( %s::%s PROPERTIES", project.getName(), libraryTarget);
    final Directory installDir = project.getLayout().getBuildDirectory().get()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName(), buildConfig));
    write(1, "IMPORTED_LOCATION %s/%s", installDir.getAsFile().toURI().getPath(),
        outputName);
    write(1, "IMPORTED_LOCATION_%s %s/%s", buildConfig.toUpperCase(), installDir.getAsFile().toURI().getPath(),
        outputName);
    write(1, "IMPORTED_CONFIGURATIONS \"%s\"", buildConfig);
    write(1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getHeaders()) {
      final File includeDir = project.getLayout().getProjectDirectory().dir(include).getAsFile();
      write(2, "%s", includeDir.toURI().getPath());
    }
    if (!library.getPublicCompileOptions().isEmpty()) {
      write(1, "INTERFACE_COMPILE_OPTIONS \"%s\"",
          String.join(";", library.getPublicCompileOptions()));
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      write(1, "INTERFACE_COMPILE_DEFINITIONS \"%s\"",
          String.join(";", library.getPublicCompileDefinitions()));
    }
    if (!library.getPublicSystemPackageDependencies().isEmpty()) {
      write(1, "INTERFACE_LINK_LIBRARIES \"%s\"",
          String.join(";", library.getPublicSystemPackageDependencies()));
    }
    if (!library.getPublicProjectPackageDependencies().isEmpty()) {
      for (final CMakeResolvedProjectPackageDependency projectDependency : library
          .getPublicProjectPackageDependencies()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s-%s;\"", projectDependency.getName(),
            projectDependency.getType().name().toLowerCase());
      }
    }
    if (!library.getPublicLinkOptions().isEmpty()) {
      write(1, "INTERFACE_LINK_LIBRARIES \"%s\"",
          String.join(";", library.getPublicLinkOptions()));
    }
    write(")");
  }
}
