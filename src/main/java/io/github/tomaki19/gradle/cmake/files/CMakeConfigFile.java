/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Project;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProject;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeConfigFile extends CMakeFileContent {

  private final CMakeResolvedToolchain toolchain;

  public CMakeConfigFile(final CMakeResolvedToolchain toolchain, final Project project) throws FileNotFoundException {
    super(project);
    this.toolchain = toolchain; // SpotBugs: toolchain is a resolved object, not meant to be mutated by plugin
  }

  public static String name(final String buildTarget) {
    return "%s-config.cmake".formatted(buildTarget);
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    write(outputStream, "include( CMakeFindDependencyMacro )");
    writeSystemPackageDependencies(outputStream, 0, toolchain);
    writeProjectPackageDependencies(outputStream, 0, toolchain);
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      for (final CMakeResolvedLibrary library : toolchain.getInterfaceLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain.getName(),
            CMakeLinkage.INTERFACE.toString(), buildConfig);
        writeLine(outputStream);
        write(outputStream, "if( NOT TARGET %s::%s )", getProjectName(), libraryTarget);
        write(outputStream, 1, "add_library( %s::%s INTERFACE IMPORTED )", getProjectName(), libraryTarget);
        setTargetProperties(outputStream, library, libraryTarget);
        write(outputStream, "endif()");
      }
      for (final CMakeResolvedLibrary library : toolchain.getStaticLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain.getName(),
            CMakeLinkage.STATIC.toString(), buildConfig);
        final String outputName = toolchain.getOperatingSystem().getStaticLibraryName(library.getOutputName());
        writeLine(outputStream);
        write(outputStream, "if( NOT TARGET %s::%s )", getProjectName(), libraryTarget);
        write(outputStream, 1, "add_library( %s::%s STATIC IMPORTED )", getProjectName(), libraryTarget);
        setTargetProperties(outputStream, library, libraryTarget, outputName, toolchain, buildConfig);
        write(outputStream, "endif()");
      }
      for (final CMakeResolvedLibrary library : toolchain.getSharedLibraries()) {
        final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain.getName(),
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

  private void writeSystemPackageDependencies(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedToolchain toolchain) throws IOException {
    for (final CMakeResolvedPackage resolvedPackage : toolchain.getPackages()) {
      writeLine(outputStream);
      write(outputStream, "if( NOT TARGET %s )", resolvedPackage.getName());
      for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
        write(outputStream, indent, "set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
            property.getValue().toUpperCase());
      }
      if (resolvedPackage.getComponents().isEmpty()) {
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, indent, "find_dependency( %s REQUIRED CONFIG )",
              resolvedPackage.getName());
        } else {
          write(outputStream, indent, "find_dependency( %s REQUIRED )",
              resolvedPackage.getName());
        }
      } else {
        write(outputStream, indent, "find_dependency( %s REQUIRED", resolvedPackage.getName());
        write(outputStream, indent + 1, "COMPONENTS");
        for (final String component : resolvedPackage.getComponents()) {
          write(outputStream, indent + 2, component);
        }
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, indent, "CONFIG");
        }
        write(outputStream, indent, ")");
      }
      write(outputStream, "endif()");
    }
  }

  private void writeProjectPackageDependencies(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedToolchain toolchain) throws IOException {
    for (final CMakeResolvedProject resolvedProject : toolchain.getProjects()) {
      if (!Objects.equals(getProjectName(), resolvedProject.getName())) {
        final String target = CMakeFileConventions.cmakeConfigName(resolvedProject.getName(), toolchain.getName());
        write(outputStream, indent + 1, "find_dependency( %s CONFIG REQUIRED", target);
        write(outputStream, indent + 2, "NO_CMAKE_ENVIRONMENT_PATH");
        write(outputStream, indent + 2, "NO_CMAKE_FIND_ROOT_PATH");
        write(outputStream, indent + 2, "NO_CMAKE_SYSTEM_PATH");
        write(outputStream, indent + 2, "NO_SYSTEM_ENVIRONMENT_PATH");
        write(outputStream, indent + 1, ")");
      }
    }
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String objectTarget) throws IOException {
    write(outputStream, 1, "set_target_properties( %s::%s PROPERTIES", getProjectName(), objectTarget);
    write(outputStream, 2, "INTERFACE_INCLUDE_DIRECTORIES");
    final Path exportPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_EXPORT_PATH).getAsFile().toPath();
    for (final File headerDir : library.getHeaders()) {
      write(outputStream, 3, "\"${CMAKE_CURRENT_LIST_DIR}/%s\"",
          exportPath.relativize(headerDir.toPath()));
    }
    write(outputStream, 1, ")");
  }

  private void setTargetProperties(final FileOutputStream outputStream, final CMakeResolvedLibrary library,
      final String libraryTarget, final String outputName, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    write(outputStream, 1, "set_target_properties( %s::%s PROPERTIES", getProjectName(), libraryTarget);
    final Path exportPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_EXPORT_PATH).getAsFile().toPath();
    final Path installPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_EXPORT_PATH)
        .dir(toolchain.getName()).dir(buildConfig).dir("lib").getAsFile().toPath();
    write(outputStream, 2, "IMPORTED_LOCATION \"${CMAKE_CURRENT_LIST_DIR}/%s/%s\"",
        exportPath.relativize(installPath), outputName);
    write(outputStream, 2, "IMPORTED_LOCATION_%s \"${CMAKE_CURRENT_LIST_DIR}/%s/%s\"",
        buildConfig.toString().toUpperCase(), exportPath.relativize(installPath), outputName);
    write(outputStream, 2, "IMPORTED_CONFIGURATIONS %s", buildConfig.toUpperCase());
    write(outputStream, 2, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final File headerDir : library.getHeaders()) {
      write(outputStream, 3, "\"${CMAKE_CURRENT_LIST_DIR}/%s\"",
          exportPath.relativize(headerDir.toPath()));
    }

    if (!library.getPublicCompileOptions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_COMPILE_OPTIONS");
      write(outputStream, 3, "\"%s\"", String.join(";", library.getPublicCompileOptions()));
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_COMPILE_DEFINITIONS");
      write(outputStream, 3, "\"%s\"", String.join(";", library.getPublicCompileDefinitions()));
    }
    if (!library.getPublicProjectPackageDependencies().isEmpty()
        || !library.getPublicSystemPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      write(outputStream, 2, "INTERFACE_LINK_LIBRARIES");
      for (final CMakeResolvedProjectDependency projectDependency : library.getPublicProjectPackageDependencies()) {
        write(outputStream, 3, "%s::%s", projectDependency.getProject().getName(),
            CMakeFileConventions.buildTarget(projectDependency.getName(), toolchain.getName(),
                projectDependency.getLinkage(), buildConfig));
      }
      if (!library.getPublicSystemPackageDependencies().isEmpty()) {
        write(outputStream, 3, "\"%s\"", String.join(";", library.getPublicSystemPackageDependencies()));
      }
      if (!library.getPublicLinkOptions().isEmpty()) {
        write(outputStream, 3, "\"%s\"", String.join(";", library.getPublicLinkOptions()));
      }
    }
    write(outputStream, 1, ")");
  }
}
