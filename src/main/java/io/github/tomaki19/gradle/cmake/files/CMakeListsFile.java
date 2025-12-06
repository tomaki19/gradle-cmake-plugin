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
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProject;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeListsFile extends CMakeFileContent {

  public static final String NAME = "CMakeLists.txt";

  private static final String CMAKE_MINIMUM_VERSION = "3.21";

  private final Collection<CMakeResolvedToolchain> toolchains;

  public CMakeListsFile(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws FileNotFoundException {
    super(project.getLayout().getProjectDirectory().file(NAME), project);
    this.toolchains = toolchains;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    writeHeader(outputStream);
    writeDependencies(outputStream, toolchains);
    writeLibraries(outputStream, toolchains);
    writeApplications(outputStream, toolchains);
    writeTests(outputStream, toolchains);
  }

  private void writeHeader(final FileOutputStream outputStream) throws IOException {
    write(outputStream, "cmake_minimum_required( VERSION %s )", CMAKE_MINIMUM_VERSION);
    writeLine(outputStream);
    write(outputStream, "project( %s LANGUAGES C CXX )", getProjectName());
    writeLine(outputStream);
    write(outputStream, """
        set( CMAKE_EXPORT_COMPILE_COMMANDS ON CACHE INTERNAL "" )
        set( CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON CACHE INTERNAL "" )
        set( CMAKE_CONFIGURATION_TYPES $ENV{CMAKE_CONFIGURATION_TYPES} )
        set( CMAKE_TOOLCHAIN_FILE $ENV{CMAKE_TOOLCHAIN_FILE} )
        set( CMAKE_BUILD_TYPE $ENV{CMAKE_BUILD_TYPE} )
        set( CMAKE_TOOLCHAIN_NAME $ENV{CMAKE_TOOLCHAIN_NAME} )
        """);
  }

  private void writeDependencies(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getPackages().isEmpty() || !toolchain.getProjects().isEmpty()) {
        writeLine(outputStream);
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        writeProjectDependencies(outputStream, 1, toolchain.getProjects(), toolchain);
        writePackageDependencies(outputStream, 1, toolchain.getPackages());
        write(outputStream, "endif()");
      }
    }
  }

  private void writeProjectDependencies(final FileOutputStream outputStream, final int indent,
      final Collection<CMakeResolvedProject> dependencies, final CMakeResolvedToolchain toolchain) throws IOException {
    for (final CMakeResolvedProject resolvedProject : dependencies) {
      if (!Objects.equals(getProjectName(), resolvedProject.getName())) {
        final String target = CMakeFileConventions.cmakeConfigName(resolvedProject.getName(), toolchain);
        write(outputStream, indent, "if( NOT TARGET %s )", target);
        write(outputStream, indent + 1, "set( %s_DIR \"%s\" )", target, resolvedProject.getBuildDirectory()
            .dir(CMakeFileConventions.CMAKE_INSTALL_PATH).getAsFile().toURI().getPath());
        write(outputStream, indent + 1, "find_package( %s CONFIG REQUIRED )", target);
        write(outputStream, indent, "endif()");
      }
    }
  }

  private void writePackageDependencies(final FileOutputStream outputStream, final int indent,
      final Collection<CMakeResolvedPackage> dependencies) throws IOException {
    for (final CMakeResolvedPackage resolvedPackage : dependencies) {
      write(outputStream, indent, "if( NOT TARGET %s )", resolvedPackage.getName());
      for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
        write(outputStream, indent + 1, "set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
            property.getValue().toUpperCase());
      }
      if (resolvedPackage.getComponents().isEmpty()) {
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, indent + 1, "find_package( %s REQUIRED CONFIG )", resolvedPackage.getName());
        } else {
          write(outputStream, indent + 1, "find_package( %s REQUIRED )", resolvedPackage.getName());
        }
      } else {
        write(outputStream, indent + 1, "find_package( %s REQUIRED", resolvedPackage.getName());
        write(outputStream, indent + 2, "COMPONENTS");
        for (final String component : resolvedPackage.getComponents()) {
          write(outputStream, indent + 3, component);
        }
        if (resolvedPackage.isConfigMode()) {
          write(outputStream, indent + 2, "CONFIG");
        }
        write(outputStream, indent + 1, ")");
      }
      write(outputStream, indent, "endif()");
    }
  }

  private void writeLibraries(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getInterfaceLibraries().isEmpty() || !toolchain.getStaticLibraries().isEmpty()
          || !toolchain.getSharedLibraries().isEmpty()) {
        writeLine(outputStream);
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedLibrary component : toolchain.getInterfaceLibraries()) {
            writeInterfaceLibrary(outputStream, 1, component, toolchain, buildConfig);
          }
          for (final CMakeResolvedLibrary component : toolchain.getStaticLibraries()) {
            writeStaticLibrary(outputStream, 1, component, toolchain, buildConfig);
          }
          for (final CMakeResolvedLibrary component : toolchain.getSharedLibraries()) {
            writeSharedLibrary(outputStream, 1, component, toolchain, buildConfig);
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeApplications(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getApplications().isEmpty()) {
        writeLine(outputStream);
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable component : toolchain.getApplications()) {
            final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain, buildConfig);
            writeExecutable(outputStream, 1, target, component, toolchain, buildConfig);
            final Directory installDir = getBuildDirectory()
                .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
            final String outputName = component.getOutputName();
            writeOutputTargetProperties(outputStream, 1, target, installDir, outputName, toolchain, buildConfig);
            if (component.isStripDebug()) {
              writeStripDebugCommand(outputStream, 1, target);
            }
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeTests(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getTests().isEmpty()) {
        writeLine(outputStream);
        write(outputStream, "enable_testing()");
        write(outputStream, "include( CTest )");
        writeLine(outputStream);
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable component : toolchain.getTests()) {
            final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain, buildConfig);
            writeExecutable(outputStream, 1, target, component, toolchain, buildConfig);
            final Directory installDir = getBuildDirectory()
                .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName()));
            writeOutputTargetProperties(outputStream, 1, target, installDir, component.getOutputName(), toolchain,
                buildConfig);
            writeAddTest(outputStream, 1, target, component.getOutputName());
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeInterfaceLibrary(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain,
        CMakeLinkage.INTERFACE.toString(),
        buildConfig);
    write(outputStream, indent, "add_library( %s INTERFACE )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, indent, target, "INTERFACE", component.getHeaders());
    writePublicCompiling(outputStream, indent, target, component);
    writePublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
  }

  private void writeStaticLibrary(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary component,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain,
        CMakeLinkage.STATIC.toString(),
        buildConfig);
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
    final String outputName = component.getOutputName();
    write(outputStream, indent, "add_library( %s STATIC )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetSources(outputStream, indent, target, "PUBLIC", component.getSources());
    writeTargetIncludeDirectories(outputStream, indent, target, "PUBLIC", component.getHeaders());
    writePrivateCompiling(outputStream, indent, target, component);
    writePublicCompiling(outputStream, indent, target, component);
    writePrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writePublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeOutputTargetProperties(outputStream, indent, target, installDir, outputName, toolchain, buildConfig);
    if (component.isStripDebug()) {
      writeStripDebugCommand(outputStream, indent, target);
    }
  }

  private void writeSharedLibrary(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain,
        CMakeLinkage.SHARED.toString(),
        buildConfig);
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
    write(outputStream, indent, "add_library( %s SHARED )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, indent, target, "PUBLIC", component.getHeaders());
    writeTargetSources(outputStream, indent, target, "PUBLIC", component.getSources());
    writePrivateCompiling(outputStream, indent, target, component);
    writePublicCompiling(outputStream, indent, target, component);
    writePrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writePublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeOutputTargetProperties(outputStream, indent, target, installDir, component.getOutputName(), toolchain,
        buildConfig);
    if (component.isStripDebug()) {
      writeStripDebugCommand(outputStream, indent, target);
    }
  }

  private void writeExecutable(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    write(outputStream, indent, "add_executable( %s )", target);
    writeTargetIncludeDirectories(outputStream, indent, target, "PUBLIC", component.getHeaders());
    writeTargetSources(outputStream, indent, target, "PRIVATE", component.getSources());
    writePrivateCompiling(outputStream, indent, target, component);
    writePrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
  }

  private void writeAddTest(final FileOutputStream outputStream, final int indent, final String target,
      final String outputName) throws IOException {
    write(outputStream, indent, "add_test(");
    write(outputStream, indent + 1, "NAME %s", target);
    write(outputStream, indent + 1, "COMMAND $<TARGET_FILE:%s>", target);
    write(outputStream, indent, ")");
  }

  private void writeTargetIncludeDirectories(final FileOutputStream outputStream, final int indent, final String target,
      final String access, final Collection<File> headers) throws IOException {
    write(outputStream, indent, "target_include_directories( %s %s", target, access);
    final File workingDir = getProjectDirectory().getAsFile();
    for (final File headerDir : headers) {
      write(outputStream, indent + 1, "${CMAKE_CURRENT_SOURCE_DIR}/%s",
          workingDir.toPath().relativize(headerDir.toPath()));
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetSources(final FileOutputStream outputStream, final int indent, final String target,
      final String access,
      final Collection<File> sources) throws IOException {
    write(outputStream, indent, "target_sources( %s %s", target, access);
    final File workingDir = getProjectDirectory().getAsFile();
    for (final File sourceFile : sources) {
      write(outputStream, indent + 1, "${CMAKE_CURRENT_SOURCE_DIR}/%s",
          workingDir.toPath().relativize(sourceFile.toPath()));
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetCompileOptions(final FileOutputStream outputStream, final int indent, final String target,
      final String access,
      final Collection<String> options) throws IOException {
    write(outputStream, indent, "target_compile_options( %s %s", target, access);
    for (final String option : options) {
      write(outputStream, indent + 1, option);
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetCompileDefinitions(final FileOutputStream outputStream, final int indent, final String target,
      final String type, final Collection<String> definitions) throws IOException {
    write(outputStream, indent, "target_compile_definitions( %s %s", target, type);
    for (final String definition : definitions) {
      write(outputStream, indent + 1, definition);
    }
    write(outputStream, indent, ")");
  }

  private void writePrivateCompiling(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary) throws IOException {
    if (!binary.getPrivateCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, indent, target, "PRIVATE", binary.getPrivateCompileOptions());
    }
    if (!binary.getPrivateCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, indent, target, "PRIVATE", binary.getPrivateCompileDefinitions());
    }
  }

  private void writePublicCompiling(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedLibrary library) throws IOException {
    if (!library.getPublicCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, indent, target, "PUBLIC", library.getPublicCompileOptions());
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, indent, target, "PUBLIC", library.getPublicCompileDefinitions());
    }
  }

  private void writePrivateLinking(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    if (!binary.getPrivateSystemPackageDependencies().isEmpty()
        || !binary.getPrivateProjectPackageDependencies().isEmpty()
        || !binary.getPrivateLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(outputStream, indent, target, "PRIVATE", toolchain, buildConfig,
          binary.getPrivateProjectPackageDependencies(),
          binary.getPrivateSystemPackageDependencies(),
          binary.getPrivateLinkOptions());
    }
  }

  private void writePublicLinking(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    if (!library.getPublicProjectDependencies().isEmpty()
        || !library.getPublicPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(outputStream, indent, target, "PUBLIC", toolchain, buildConfig,
          library.getPublicProjectDependencies(),
          library.getPublicPackageDependencies(),
          library.getPublicLinkOptions());
    }
  }

  private void writeTargetLinkLibraries(final FileOutputStream outputStream, final int indent, final String target,
      final String type,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> projectPackageDependencies,
      final Collection<String> packageDependencies, final Collection<String> options) throws IOException {
    write(outputStream, indent, "target_link_libraries( %s %s", target, type);
    for (final CMakeResolvedProjectDependency projectModule : projectPackageDependencies) {
      write(outputStream, indent + 1, "%s::%s", projectModule.getProject().getName(),
          CMakeFileConventions.buildTarget(projectModule.getName(), toolchain, projectModule.getLinkage(),
              buildConfig));
    }
    for (final String packageDependency : packageDependencies) {
      write(outputStream, indent + 1, packageDependency);
    }
    for (final String option : options) {
      write(outputStream, indent + 1, option);
    }
    write(outputStream, indent, ")");
  }

  private void writeOutputTargetProperties(final FileOutputStream outputStream, final int indent, final String target,
      final Directory installDir, final String outputName, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    final File projectDir = getProjectDirectory().getAsFile();
    write(outputStream, indent, "set_target_properties( %s PROPERTIES", target);
    write(outputStream, indent + 1, "OUTPUT_NAME \"%s\"", outputName);
    write(outputStream, indent + 1, "ARCHIVE_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
        projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "ARCHIVE_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
          config.toString().toUpperCase(),
          projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    }
    write(outputStream, indent + 1, "LIBRARY_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
        projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "LIBRARY_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
          config.toString().toUpperCase(),
          projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    }
    write(outputStream, indent + 1, "RUNTIME_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
        projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "RUNTIME_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s\"",
          config.toString().toUpperCase(),
          projectDir.toPath().relativize(installDir.dir(buildConfig.toString()).getAsFile().toPath()));
    }
    write(outputStream, indent, ")");
  }

  private void writeStripDebugCommand(final FileOutputStream outputStream, final int indent, final String target)
      throws IOException {
    write(outputStream, indent, "add_custom_command( TARGET %s POST_BUILD", target);
    write(outputStream, indent + 1, "COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:%s> $<TARGET_FILE:%s>.debug",
        target, target);
    write(outputStream, indent + 1, "COMMAND ${CMAKE_STRIP} -g $<TARGET_FILE:%s>", target);
    write(outputStream, indent, ")");
  }
}
