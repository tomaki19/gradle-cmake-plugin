/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedFindPackageDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

public class CMakeConfigFile extends CMakeFileOutputStream {

  public CMakeConfigFile(final Project project) throws FileNotFoundException {
    super(
        project
            .getLayout()
            .getBuildDirectory()
            .dir(CMakeListsConventions.CMAKE_INSTALL_PATH)
            .get()
            .file("%s-config.cmake".formatted(project.getName().toLowerCase())));
  }

  @Override
  public void write(final CMakeResolvedBuild build, final Project project) throws IOException {
    for (final CMakeResolvedLibrary library : build.getLibraries()) {
      if (library.getSources().isEmpty()) {
        final String libraryTarget =
            CMakeListsConventions.interfaceLibraryTarget(
                library.getName(), library.getToolchain(), library.getBuildConfig());
        write("add_library( %s::%s INTERFACE IMPORTED )", project.getName(), libraryTarget);
        setTargetProperties(library, libraryTarget, "", project);
      } else {
        if (library.isBuildStatic()) {
          final String libraryTarget =
              CMakeListsConventions.staticLibraryTarget(
                  library.getName(), library.getToolchain(), library.getBuildConfig());
          write("add_library( %s::%s STATIC IMPORTED )", project.getName(), libraryTarget);
          setTargetProperties(
              library, libraryTarget, OperatingSystem.current().getStaticLibrarySuffix(), project);
        }
        if (library.isBuildShared()) {
          final String libraryTarget =
              CMakeListsConventions.sharedLibraryTarget(
                  library.getName(), library.getToolchain(), library.getBuildConfig());
          write("add_library( %s::%s SHARED IMPORTED )", project.getName(), libraryTarget);
          setTargetProperties(
              library, libraryTarget, OperatingSystem.current().getSharedLibrarySuffix(), project);
        }
      }
    }
  }

  private void setTargetProperties(
      final CMakeResolvedLibrary library,
      final String libraryTarget,
      final String librarySuffix,
      final Project project)
      throws IOException {
    write("set_target_properties( %s::%s PROPERTIES", project.getName(), libraryTarget);
    final File installDir =
        project
            .getLayout()
            .getBuildDirectory()
            .get()
            .dir(
                "%s/%s"
                    .formatted(
                        CMakeListsConventions.CMAKE_INSTALL_PATH, library.getToolchain().getName()))
            .getAsFile();
    for (final String buildConfig : library.getToolchain().getBuildConfigs()) {
      write(
          1,
          "IMPORTED_LOCATION_%s %s/%s%s",
          buildConfig.toUpperCase(),
          installDir.toURI().getPath(),
          libraryTarget,
          librarySuffix);
    }
    write(
        1, "IMPORTED_LOCATION %s/%s%s", installDir.toURI().getPath(), libraryTarget, librarySuffix);
    for (final String buildConfig : library.getToolchain().getBuildConfigs()) {
      write(1, "IMPORTED_CONFIGURATIONS \"%s;\"", buildConfig);
    }
    write(1, "INTERFACE_INCLUDE_DIRECTORIES");
    for (final String include : library.getIncludes()) {
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
    if (!library.getPublicFindPackageDependencies().isEmpty()
        || !library.getPublicProjectModuleDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      for (final CMakeResolvedFindPackageDependency findPackageDependency :
          library.getPublicFindPackageDependencies()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s;\"", findPackageDependency.getBuildTarget());
      }
      for (final CMakeResolvedProjectModuleDependency projectModuleDependency :
          library.getPublicProjectModuleDependencies()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s;\"", projectModuleDependency.getBuildTarget());
      }
      for (final String linkOption : library.getPublicLinkOptions()) {
        write(1, "INTERFACE_LINK_LIBRARIES \"%s;\"", linkOption);
      }
    }
    write(")");
  }
}
