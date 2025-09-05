/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeConfigFile extends CMakeFileContent {

  private final CMakeResolvedToolchain toolchain;

  public CMakeConfigFile(final CMakeResolvedToolchain toolchain, final Project project) throws FileNotFoundException {
    super(project.getLayout().getBuildDirectory().dir(CMakeFileConventions.CMAKE_BUILD_PATH).get()
        .file("%s-config.cmake".formatted(CMakeFileConventions.cmakeConfigName(project.getName(), toolchain))),
        project);
    this.toolchain = toolchain;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
        if (library.getSources().isEmpty()) {
          final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
              CMakeLinkType.INTERFACE, buildConfig);
          writeLine(outputStream);
          write(outputStream, "add_library( %s::%s INTERFACE IMPORTED )", getProjectName(), libraryTarget);
          setTargetProperties(outputStream, library, libraryTarget);
        } else {
          if (library.isBuildStatic()) {
            final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
                CMakeLinkType.STATIC, buildConfig);
            final String outputName = toolchain.getOperatingSystem().getStaticLibraryName(library.getOutputName());
            writeLine(outputStream);
            write(outputStream, "add_library( %s::%s STATIC IMPORTED )", getProjectName(), libraryTarget);
            setTargetProperties(outputStream, library, libraryTarget, outputName, toolchain, buildConfig);
          }
          if (library.isBuildShared()) {
            final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
                CMakeLinkType.SHARED, buildConfig);
            final String outputName = toolchain.getOperatingSystem().getSharedLibraryName(library.getOutputName());
            writeLine(outputStream);
            write(outputStream, "add_library( %s::%s SHARED IMPORTED )", getProjectName(), libraryTarget);
            setTargetProperties(outputStream, library, libraryTarget, outputName, toolchain, buildConfig);
          }
        }
      }
    }
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String objectTarget) throws IOException {
    write(outputStream, "set_target_properties( %s::%s PROPERTIES", getProjectName(), objectTarget);
    write(outputStream, 1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getHeaders()) {
      final File includeDir = getProjectDirectory().dir(include).getAsFile();
      write(outputStream, 2, "%s", includeDir.toURI().getPath());
    }
    write(outputStream, ")");
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String libraryTarget, final String outputName, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    write(outputStream, "set_target_properties( %s::%s PROPERTIES", getProjectName(), libraryTarget);
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName(), buildConfig));
    write(outputStream, 1, "IMPORTED_LOCATION %s/%s", installDir.getAsFile().toURI().getPath(),
        outputName);
    write(outputStream, 1, "IMPORTED_LOCATION_%s %s/%s", buildConfig.toUpperCase(),
        installDir.getAsFile().toURI().getPath(),
        outputName);
    write(outputStream, 1, "IMPORTED_CONFIGURATIONS \"%s\"", buildConfig);
    write(outputStream, 1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getHeaders()) {
      final File includeDir = getProjectDirectory().dir(include).getAsFile();
      write(outputStream, 2, "%s", includeDir.toURI().getPath());
    }
    if (!library.getPublicCompileOptions().isEmpty()) {
      write(outputStream, 1, "INTERFACE_COMPILE_OPTIONS \"%s\"",
          String.join(";", library.getPublicCompileOptions()));
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      write(outputStream, 1, "INTERFACE_COMPILE_DEFINITIONS \"%s\"",
          String.join(";", library.getPublicCompileDefinitions()));
    }
    if (!library.getPublicSystemPackageDependencies().isEmpty()) {
      write(outputStream, 1, "INTERFACE_LINK_LIBRARIES \"%s\"",
          String.join(";", library.getPublicSystemPackageDependencies()));
    }
    if (!library.getPublicProjectPackageDependencies().isEmpty()) {
      for (final CMakeResolvedProjectPackageDependency projectDependency : library
          .getPublicProjectPackageDependencies()) {
        write(outputStream, 1, "INTERFACE_LINK_LIBRARIES \"%s-%s;\"", projectDependency.getName(),
            projectDependency.getType().name().toLowerCase());
      }
    }
    if (!library.getPublicLinkOptions().isEmpty()) {
      write(outputStream, 1, "INTERFACE_LINK_LIBRARIES \"%s\"",
          String.join(";", library.getPublicLinkOptions()));
    }
    write(outputStream, ")");
  }
}
