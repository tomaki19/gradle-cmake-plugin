/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import org.gradle.api.file.Directory;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeModuleFile extends CMakeFileContent {

  private final CMakeResolvedLibrary library;
  private final CMakeResolvedToolchain toolchain;
  private final String buildConfig;

  public CMakeModuleFile(final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final String projectName, final Directory projectDirectory,
      final Directory buildDirectory) {
    super("%s.cmake".formatted(CMakeFileConventions.moduleTarget(projectName, library.getName(),
        library.getLinkType(), toolchain.getName(), buildConfig)), projectName, projectDirectory,
        buildDirectory);
    this.library = library;
    this.toolchain = toolchain;
    this.buildConfig = buildConfig;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    final String target = CMakeFileConventions.buildTarget(getProjectName(), library.getName(),
        library.getLinkType(), toolchain.getName(), buildConfig);
    write(outputStream, 0, "if( NOT TARGET %s )", target);
    writePackageDependencies(outputStream, 0, library.getAllPackageDependencies());
    writeProjectDependencies(outputStream, 0, library.getAllProjectDependencies(), toolchain, buildConfig);
    write(outputStream, 1, "add_library( %s %s IMPORTED )", target, library.getLinkType().name());
    writeTargetProperties(outputStream, 1, library, target, buildConfig);
    write(outputStream, 0, "endif()");
  }

  protected void writePackageDependencies(final FileOutputStream outputStream, final int indent,
      final Collection<CMakeResolvedPackageDependency> dependencies) throws IOException {
    for (final CMakeResolvedPackageDependency dependency : dependencies) {
      final CMakeResolvedPackage resolvedPackage = dependency.getResolvedPackage();
      write(outputStream, indent, "find_package( %s REQUIRED", resolvedPackage.getName());
      if (resolvedPackage.isModuleMode()) {
        write(outputStream, indent + 1, "MODULE");
      } else {
        write(outputStream, indent + 1, "CONFIG");
      }
      if (!resolvedPackage.getComponents().isEmpty()) {
        write(outputStream, indent + 1, "COMPONENTS");
        for (final String component : resolvedPackage.getComponents()) {
          write(outputStream, indent + 2, component);
        }
      }
      write(outputStream, indent, ")");
      for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
        write(outputStream, indent, "set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
            property.getValue().toUpperCase());
      }
    }
  }

  protected void writeProjectDependencies(final FileOutputStream outputStream, final int indent,
      final Collection<CMakeResolvedProjectDependency> dependencies, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    for (final CMakeResolvedProjectDependency dependency : dependencies) {
      write(outputStream, indent, "include( %s )", CMakeFileConventions.moduleTarget(dependency.getProjectName(),
          dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig));
    }
  }

  private void writeTargetProperties(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary library, final String target, final String buildConfig) throws IOException {
    final Path targetPath = Paths.get(toolchain.getName(), buildConfig,
        CMakeFileConventions.buildTarget(library.getName(), library.getLinkType(), toolchain.getName(), buildConfig));
    switch (library.getLinkType()) {
      case SHARED: {
        write(outputStream, indent, "set_target_properties( %s PROPERTIES", target);
        write(outputStream, indent + 1, "IMPORTED_CONFIGURATIONS %s", buildConfig.toUpperCase());
        write(outputStream, indent + 1, "IMPORTED_LOCATION_%s \"${CMAKE_CURRENT_LIST_DIR}/%s/lib/%s\"",
            buildConfig.toUpperCase(), targetPath,
            OperatingSystem.current().getSharedLibraryName(library.getOutputName()));
        if (OperatingSystem.current().isLinux()) {
          write(outputStream, indent + 1, "IMPORTED_SONAME_%s \"%s\"", buildConfig.toUpperCase(),
              OperatingSystem.current().getLinkLibraryName(library.getOutputName()));
        } else if (OperatingSystem.current().isWindows()) {
          write(outputStream, indent + 1,
              "IMPORTED_IMPLIB_%s \"${CMAKE_CURRENT_LIST_DIR}/%s/%s/lib\"", buildConfig.toUpperCase(), targetPath,
              OperatingSystem.current().getLinkLibraryName(library.getOutputName()));
        }
        write(outputStream, indent, ")");
        break;
      }
      case STATIC: {
        write(outputStream, indent, "set_target_properties( %s PROPERTIES", target);
        write(outputStream, indent + 1, "IMPORTED_CONFIGURATIONS %s", buildConfig.toUpperCase());
        write(outputStream, indent + 1, "IMPORTED_LOCATION_%s \"${CMAKE_CURRENT_LIST_DIR}/%s/%s/lib\"",
            buildConfig.toUpperCase(), targetPath,
            OperatingSystem.current().getStaticLibraryName(library.getOutputName()));
        write(outputStream, indent, ")");
        break;
      }
      default:
        break;
    }
    for (final File headerDir : library.getHeaders()) {
      final Path exportPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH).getAsFile().toPath();
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_INCLUDE_DIRECTORIES \"${CMAKE_CURRENT_LIST_DIR}/%s\"",
          exportPath.relativize(headerDir.toPath()));
      write(outputStream, indent, ")");
    }
    for (final String compileOption : library.getPublicCompileOptions()) {
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_COMPILE_OPTIONS %s", compileOption);
      write(outputStream, indent, ")");
    }
    for (final String compileDefinition : library.getPrivateCompileDefinitions()) {
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_COMPILE_DEFINITIONS %s", compileDefinition);
      write(outputStream, indent, ")");
    }
    for (final CMakeResolvedProjectDependency dependency : library.getPublicProjectDependencies()) {
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_LINK_LIBRARIES %s",
          CMakeFileConventions.buildTarget(dependency.getProjectName(), dependency.getName(), dependency.getLinkType(),
              toolchain.getName(), buildConfig));
      write(outputStream, indent, ")");
    }
    for (final CMakeResolvedPackageDependency dependency : library.getPublicPackageDependencies()) {
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_LINK_LIBRARIES %s::%s", dependency.getTargetPrefix(),
          dependency.getName());
      write(outputStream, indent, ")");
    }
    for (final String option : library.getPublicLinkOptions()) {
      write(outputStream, indent, "set_property( TARGET %s APPEND PROPERTY", target);
      write(outputStream, indent + 1, "INTERFACE_LINK_LIBRARIES %s", option);
      write(outputStream, indent, ")");
    }
  }

}
