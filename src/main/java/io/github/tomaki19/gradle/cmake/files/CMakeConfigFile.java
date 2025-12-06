/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProject;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeConfigFile extends CMakeFileContent {

  private final CMakeResolvedToolchain toolchain;

  public CMakeConfigFile(final CMakeResolvedToolchain toolchain, final Project project) throws FileNotFoundException {
    super(project.getLayout().getBuildDirectory().dir(CMakeFileConventions.CMAKE_INSTALL_PATH).get()
        .file("%s-config.cmake".formatted(CMakeFileConventions.cmakeConfigName(project.getName(), toolchain))),
        project);
    this.toolchain = toolchain;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    write(outputStream, "include( CMakeFindDependencyMacro )");
    writePackageDependencies(outputStream, toolchain.getPackages());
    writeProjectDependencies(outputStream, toolchain.getProjects(), toolchain);
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
            CMakeLinkage.INTERFACE.toString(), buildConfig);
        writeLine(outputStream);
        write(outputStream, "if( NOT TARGET %s::%s )", getProjectName(), libraryTarget);
        write(outputStream, 1, "add_library( %s::%s INTERFACE IMPORTED )", getProjectName(), libraryTarget);
        setTargetProperties(outputStream, library, libraryTarget);
        write(outputStream, "endif()");
      }
      for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
            CMakeLinkage.STATIC.toString(), buildConfig);
        final String outputName = toolchain.getOperatingSystem().getStaticLibraryName(library.getOutputName());
        writeLine(outputStream);
        write(outputStream, "if( NOT TARGET %s::%s )", getProjectName(), libraryTarget);
        write(outputStream, 1, "add_library( %s::%s STATIC IMPORTED )", getProjectName(), libraryTarget);
        setTargetProperties(outputStream, library, libraryTarget, outputName, toolchain, buildConfig);
        write(outputStream, "endif()");
      }
      for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
            CMakeLinkage.SHARED.toString(), buildConfig);
        final String outputName = toolchain.getOperatingSystem().getSharedLibraryName(library.getOutputName());
        writeLine(outputStream);
        write(outputStream, "if( NOT TARGET %s::%s )", getProjectName(), libraryTarget);
        write(outputStream, 1, "add_library( %s::%s SHARED IMPORTED )", getProjectName(), libraryTarget);
        setTargetProperties(outputStream, library, libraryTarget, outputName, toolchain, buildConfig);
        write(outputStream, "endif()");
      }
    }
  }

  private void writePackageDependencies(final FileOutputStream outputStream,
      final Collection<CMakeResolvedPackage> dependencies) throws IOException {
    for (final CMakeResolvedPackage resolvedPackage : dependencies) {
      writeLine(outputStream);
      write(outputStream, "if( NOT TARGET %s )", resolvedPackage.getName());
      for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
        write(outputStream, 1, "set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
            property.getValue().toUpperCase());
      }
      if (resolvedPackage.getComponents().isEmpty()) {
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, 1, "find_dependency( %s REQUIRED CONFIG )", resolvedPackage.getName());
        } else {
          write(outputStream, 1, "find_dependency( %s REQUIRED )", resolvedPackage.getName());
        }
      } else {
        write(outputStream, 1, "find_dependency( %s REQUIRED", resolvedPackage.getName());
        write(outputStream, 2, "COMPONENTS");
        for (final String component : resolvedPackage.getComponents()) {
          write(outputStream, 3, component);
        }
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, 1, "CONFIG");
        }
        write(outputStream, 1, ")");
      }
      write(outputStream, "endif()");
    }
  }

  private void writeProjectDependencies(final FileOutputStream outputStream,
      final Collection<CMakeResolvedProject> dependencies, final CMakeResolvedToolchain toolchain) throws IOException {
    for (final CMakeResolvedProject resolvedProject : dependencies) {
      if (!Objects.equals(getProjectName(), resolvedProject.getName())) {
        writeLine(outputStream);
        final String target = CMakeFileConventions.cmakeConfigName(resolvedProject.getName(), toolchain);
        write(outputStream, "if( NOT TARGET %s )", target);
        write(outputStream, 1, "set( %s_DIR \"%s\" )", target, resolvedProject.getBuildDirectory()
            .dir(CMakeFileConventions.CMAKE_INSTALL_PATH).getAsFile().toURI().getPath());
        write(outputStream, 1, "find_dependency( %s CONFIG REQUIRED )", target);
        write(outputStream, "endif()");
      }
    }
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String objectTarget) throws IOException {
    write(outputStream, 1, "set_target_properties( %s::%s PROPERTIES", getProjectName(), objectTarget);
    write(outputStream, 2, "INTERFACE_INCLUDE_DIRECTORIES");
    final File workingDir = getBuildDirectory().dir(CMakeFileConventions.CMAKE_INSTALL_PATH).getAsFile();
    for (final File headerDir : library.getHeaders()) {
      write(outputStream, 3, "${CMAKE_CURRENT_LIST_DIR}/%s;", workingDir.toPath().relativize(headerDir.toPath()));
    }
    write(outputStream, 1, ")");
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String libraryTarget, final String outputName, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    write(outputStream, 1, "set_target_properties( %s::%s PROPERTIES", getProjectName(), libraryTarget);
    final File workingDir = getBuildDirectory().dir(CMakeFileConventions.CMAKE_INSTALL_PATH).getAsFile();
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName(), buildConfig));
    write(outputStream, 2, "IMPORTED_LOCATION ${CMAKE_CURRENT_LIST_DIR}/%s/%s",
        workingDir.toPath().relativize(installDir.getAsFile().toPath()),
        outputName);
    write(outputStream, 2, "IMPORTED_LOCATION_%s ${CMAKE_CURRENT_LIST_DIR}/%s/%s", buildConfig.toString().toUpperCase(),
        workingDir.toPath().relativize(installDir.getAsFile().toPath()), outputName);
    write(outputStream, 2, "IMPORTED_CONFIGURATIONS \"%s\"", buildConfig);
    write(outputStream, 2, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final File headerDir : library.getHeaders()) {
      write(outputStream, 3, "${CMAKE_CURRENT_LIST_DIR}/%s;",
          workingDir.toPath().relativize(headerDir.toPath()));
    }
    if (!library.getPublicCompileOptions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_COMPILE_OPTIONS");
      for (final String value : library.getPublicCompileOptions()) {
        write(outputStream, 3, value);
      }
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_COMPILE_DEFINITIONS");
      for (final String value : library.getPublicCompileDefinitions()) {
        write(outputStream, 3, "%s;", value);
      }
    }
    if (!library.getPublicPackageDependencies().isEmpty()
        || !library.getPublicProjectDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_LINK_LIBRARIES");
      for (final CMakeResolvedProjectDependency projectDependency : library.getPublicProjectDependencies()) {
        write(outputStream, 3, "%s::%s;", projectDependency.getProject().getName(),
            CMakeFileConventions.buildTarget(projectDependency.getName(), toolchain, projectDependency.getLinkage(),
                buildConfig));
      }
      for (final String value : library.getPublicPackageDependencies()) {
        write(outputStream, 3, "%s;", value);
      }
      for (final String value : library.getPublicLinkOptions()) {
        write(outputStream, 3, "%s;", value);
      }
    }
    write(outputStream, 1, ")");
  }
}
