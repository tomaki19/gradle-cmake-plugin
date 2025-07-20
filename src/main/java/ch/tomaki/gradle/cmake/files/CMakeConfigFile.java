/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gradle.api.Project;

import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

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
    for (final CMakeResolvedLibrary library : toolchain.getLibraries()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
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
            writeLine();
            write("add_library( %s::%s STATIC IMPORTED )", project.getName(), libraryTarget);
            setTargetProperties(library, libraryTarget, toolchain.getOperatingSystem().getStaticLibrarySuffix(),
                project);
          }
          if (library.isBuildShared()) {
            final String libraryTarget = CMakeFileConventions.buildTarget(library.getName(), toolchain,
                CMakeLinkType.SHARED, buildConfig);
            writeLine();
            write("add_library( %s::%s SHARED IMPORTED )", project.getName(), libraryTarget);
            setTargetProperties(library, libraryTarget, toolchain.getOperatingSystem().getSharedLibrarySuffix(),
                project);
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
      final String librarySuffix, final Project project) throws IOException {
    write("set_target_properties( %s::%s PROPERTIES", project.getName(), libraryTarget);
    final File installDir = project.getLayout().getBuildDirectory().get()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()))
        .getAsFile();
    write(1, "IMPORTED_LOCATION %s/%s%s", installDir.toURI().getPath(), libraryTarget,
        librarySuffix);
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "IMPORTED_LOCATION_%s %s/%s%s", buildConfig.toUpperCase(), installDir.toURI().getPath(),
          libraryTarget, librarySuffix);
    }
    write(1, "IMPORTED_CONFIGURATIONS \"%s\"", String.join(";", toolchain.getBuildConfigs()));
    write(1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getHeaders()) {
      final File includeDir = project.getLayout().getProjectDirectory().dir(include).getAsFile();
      write(2, "%s", includeDir.toURI().getPath());
    }
    if (!library.getPublicCompileOptions().isEmpty()) {
      for (final String compileOption : library.getPublicCompileOptions()) {
        write(1, "INTERFACE_COMPILE_OPTIONS \"%s;\"", compileOption);
      }
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      for (final String compileDefinition : library.getPublicCompileDefinitions()) {
        write(1, "INTERFACE_COMPILE_DEFINITIONS \"%s;\"", compileDefinition);
      }
    }
    if (!library.getPublicSystemPackageDependencies().isEmpty()
        || !library.getPublicProjectPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      for (final String packageDependency : library.getPublicSystemPackageDependencies()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s;\"", packageDependency);
      }
      for (final CMakeResolvedProjectPackageDependency projectDependency : library
          .getPublicProjectPackageDependencies()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s-%s;\"", projectDependency.getName(),
            projectDependency.getType().name().toLowerCase());
      }
      for (final String linkOption : library.getPublicLinkOptions()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s;\"", linkOption);
      }
    }
    write(")");
  }
}
