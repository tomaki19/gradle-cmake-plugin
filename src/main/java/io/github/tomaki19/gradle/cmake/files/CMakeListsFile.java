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

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProject;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedSystemPackage;
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
    writeSystemPackages(outputStream, toolchains);
    writeProjectPackages(outputStream, toolchains);
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

  private void writeSystemPackages(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getSystemPackages().isEmpty()) {
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedSystemPackage resolvedPackage : toolchain.getSystemPackages()) {
          writeLine(outputStream);
          if (resolvedPackage.getComponents().isEmpty()) {
            write(outputStream, "find_package( %s CONFIG REQUIRED )", resolvedPackage.getName());
          } else {
            write(outputStream, "find_package( %s CONFIG REQUIRED", resolvedPackage.getName());
            for (final String component : resolvedPackage.getComponents()) {
              write(outputStream, 1, "%s::%s", resolvedPackage.getName(), component);
            }
            write(outputStream, ")");
          }
          if (!resolvedPackage.getProperties().isEmpty()) {
            for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
              write(outputStream, "set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
                  property.getValue().toUpperCase());
            }
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeProjectPackages(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getProjectPackages().isEmpty()) {
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedProject resolvedPackage : toolchain.getProjectPackages()) {
          if (!Objects.equals(getProjectName(), resolvedPackage.getName())) {
            final String target = CMakeFileConventions.cmakeConfigName(resolvedPackage.getName(), toolchain);
            writeLine(outputStream);
            write(outputStream, "set( %s_DIR \"%s\" )", target, resolvedPackage.getBuildDirectory()
                .dir(CMakeFileConventions.CMAKE_BUILD_PATH).getAsFile().toURI().getPath());
            write(outputStream, "find_package( %s CONFIG REQUIRED )", target);
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeLibraries(final FileOutputStream outputStream, final Collection<CMakeResolvedToolchain> toolchains)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getLibraries().isEmpty()) {
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedLibrary object : toolchain.getLibraries()) {
            if (object.getSources().isEmpty()) {
              writeInterfaceLibrary(outputStream, object, toolchain, buildConfig);
            } else {
              if (object.isBuildStatic()) {
                writeStaticLibrary(outputStream, object, toolchain, buildConfig);
              }
              if (object.isBuildShared()) {
                writeSharedLibrary(outputStream, object, toolchain, buildConfig);
              }
            }
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
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable object : toolchain.getApplications()) {
            final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, buildConfig);
            writeExecutable(outputStream, target, object, toolchain, buildConfig);
            final Directory installDir = getBuildDirectory()
                .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
            final String outputName = object.getOutputName();
            writeOutputTargetProperties(outputStream, target, installDir, outputName, toolchain, buildConfig);
            if (object.isStripDebug()) {
              writeStripDebugCommand(outputStream, target);
            }
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeTests(final FileOutputStream outputStream, final Collection<CMakeResolvedToolchain> toolchains)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getTests().isEmpty()) {
        writeLine(outputStream);
        write(outputStream, "enable_testing()");
        write(outputStream, "include( CTest )");
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable object : toolchain.getTests()) {
            final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, buildConfig);
            writeExecutable(outputStream, target, object, toolchain, buildConfig);
            final Directory installDir = getBuildDirectory()
                .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_BUILD_PATH, toolchain.getName()));
            final String outputName = object.getOutputName();
            writeOutputTargetProperties(outputStream, target, installDir, outputName, toolchain, buildConfig);
            writeAddTest(outputStream, target, outputName);
          }
        }
        write(outputStream, "endif()");
      }
    }
  }

  private void writeInterfaceLibrary(final FileOutputStream outputStream, final CMakeResolvedLibrary object,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.INTERFACE,
        buildConfig);
    writeLine(outputStream);
    write(outputStream, "add_library( %s INTERFACE )", target);
    write(outputStream, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, target, "INTERFACE", object.getHeaders());
    writePublicCompiling(outputStream, target, object);
    writePublicLinking(outputStream, target, object, toolchain, buildConfig);
  }

  private void writeStaticLibrary(final FileOutputStream outputStream, final CMakeResolvedLibrary object,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.STATIC,
        buildConfig);
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
    final String outputName = object.getOutputName();
    writeLine(outputStream);
    write(outputStream, "add_library( %s STATIC )", target);
    write(outputStream, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetSources(outputStream, target, "PUBLIC", object.getSources());
    writeTargetIncludeDirectories(outputStream, target, "PUBLIC", object.getHeaders());
    writePrivateCompiling(outputStream, target, object);
    writePublicCompiling(outputStream, target, object);
    writePrivateLinking(outputStream, target, object, toolchain, buildConfig);
    writePublicLinking(outputStream, target, object, toolchain, buildConfig);
    writeOutputTargetProperties(outputStream, target, installDir, outputName, toolchain, buildConfig);
    if (object.isStripDebug()) {
      writeStripDebugCommand(outputStream, target);
    }
  }

  private void writeSharedLibrary(final FileOutputStream outputStream, final CMakeResolvedLibrary object,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.SHARED,
        buildConfig);
    final Directory installDir = getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()));
    final String outputName = object.getOutputName();
    writeLine(outputStream);
    write(outputStream, "add_library( %s SHARED )", target);
    write(outputStream, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, target, "PUBLIC", object.getHeaders());
    writeTargetSources(outputStream, target, "PUBLIC", object.getSources());
    writePrivateCompiling(outputStream, target, object);
    writePublicCompiling(outputStream, target, object);
    writePrivateLinking(outputStream, target, object, toolchain, buildConfig);
    writePublicLinking(outputStream, target, object, toolchain, buildConfig);
    writeOutputTargetProperties(outputStream, target, installDir, outputName, toolchain, buildConfig);
    if (object.isStripDebug()) {
      writeStripDebugCommand(outputStream, target);
    }
  }

  private void writeExecutable(final FileOutputStream outputStream, final String target,
      final CMakeResolvedBinary<?> object, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    writeLine(outputStream);
    write(outputStream, "add_executable( %s )", target);
    writeTargetIncludeDirectories(outputStream, target, "PUBLIC", object.getHeaders());
    writeTargetSources(outputStream, target, "PRIVATE", object.getSources());
    writePrivateCompiling(outputStream, target, object);
    writePrivateLinking(outputStream, target, object, toolchain, buildConfig);
  }

  private void writeAddTest(final FileOutputStream outputStream, final String target, final String outputName)
      throws IOException {
    writeLine(outputStream);
    write(outputStream, "add_test(");
    write(outputStream, 1, "NAME %s", target);
    write(outputStream, 1, "COMMAND $<TARGET_FILE:%s>", target);
    write(outputStream, ")");
  }

  private void writeTargetIncludeDirectories(final FileOutputStream outputStream, final String target,
      final String access, final Collection<String> headers) throws IOException {
    write(outputStream, "target_include_directories( %s %s", target, access);
    for (final String header : headers) {
      final File directory = getProjectDirectory().dir(header).getAsFile();
      write(outputStream, 1, "%s", getProjectDirectory().getAsFile().toURI().relativize(directory.toURI()).getPath());
    }
    write(outputStream, ")");
  }

  private void writeTargetSources(final FileOutputStream outputStream, final String target, final String access,
      final Collection<String> sources) throws IOException {
    write(outputStream, "target_sources( %s %s", target, access);
    for (final String entry : sources) {
      for (final File file : getProjectDirectory().dir(entry).getAsFileTree().getFiles()) {
        write(outputStream, 1, "%s", getProjectDirectory().getAsFile().toURI().relativize(file.toURI()).getPath());
      }
    }
    write(outputStream, ")");
  }

  private void writeTargetCompileOptions(final FileOutputStream outputStream, final String target, final String access,
      final Collection<String> options) throws IOException {
    write(outputStream, "target_compile_options( %s %s", target, access);
    for (final String option : options) {
      write(outputStream, 1, option);
    }
    write(outputStream, ")");
  }

  private void writeTargetCompileDefinitions(final FileOutputStream outputStream, final String target,
      final String type, final Collection<String> definitions) throws IOException {
    write(outputStream, "target_compile_definitions( %s %s", target, type);
    for (final String definition : definitions) {
      write(outputStream, 1, definition);
    }
    write(outputStream, ")");
  }

  private void writePrivateCompiling(final FileOutputStream outputStream, final String target,
      final CMakeResolvedBinary<?> binary) throws IOException {
    if (!binary.getPrivateCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, target, "PRIVATE", binary.getPrivateCompileOptions());
    }
    if (!binary.getPrivateCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, target, "PRIVATE", binary.getPrivateCompileDefinitions());
    }
  }

  private void writePublicCompiling(final FileOutputStream outputStream, final String target,
      final CMakeResolvedLibrary library) throws IOException {
    if (!library.getPublicCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, target, "PUBLIC", library.getPublicCompileOptions());
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, target, "PUBLIC", library.getPublicCompileDefinitions());
    }
  }

  private void writePrivateLinking(final FileOutputStream outputStream, final String target,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    if (!binary.getPrivateSystemPackageDependencies().isEmpty()
        || !binary.getPrivateProjectPackageDependencies().isEmpty()
        || !binary.getPrivateLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(outputStream, target, "PRIVATE", toolchain, buildConfig,
          binary.getPrivateProjectPackageDependencies(),
          binary.getPrivateSystemPackageDependencies(),
          binary.getPrivateLinkOptions());
    }
  }

  private void writePublicLinking(final FileOutputStream outputStream, final String target,
      final CMakeResolvedLibrary library, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    if (!library.getPublicSystemPackageDependencies().isEmpty()
        || !library.getPublicProjectPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(outputStream, target, "PUBLIC", toolchain, buildConfig,
          library.getPublicProjectPackageDependencies(),
          library.getPublicSystemPackageDependencies(),
          library.getPublicLinkOptions());
    }
  }

  private void writeTargetLinkLibraries(final FileOutputStream outputStream, final String target, final String type,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectPackageDependency> projectPackageDependencies,
      final Collection<String> packageDependencies, final Collection<String> options) throws IOException {
    write(outputStream, "target_link_libraries( %s %s", target, type);
    for (final CMakeResolvedProjectPackageDependency projectModule : projectPackageDependencies) {
      write(outputStream, 1, "%s::%s", projectModule.getProject().getName(),
          CMakeFileConventions.buildTarget(projectModule.getName(), toolchain, projectModule.getType(), buildConfig));
    }
    for (final String packageDependency : packageDependencies) {
      write(outputStream, 1, packageDependency);
    }
    for (final String option : options) {
      write(outputStream, 1, option);
    }
    write(outputStream, ")");
  }

  private void writeOutputTargetProperties(final FileOutputStream outputStream, final String target,
      final Directory installDir, final String outputName, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    write(outputStream, "set_target_properties( %s PROPERTIES", target);
    write(outputStream, 1, "OUTPUT_NAME \"%s\"", outputName);
    write(outputStream, 1, "ARCHIVE_OUTPUT_DIRECTORY \"%s\"",
        installDir.dir(buildConfig).getAsFile().toURI().getPath());
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, 1, "ARCHIVE_OUTPUT_DIRECTORY_%s \"%s\"", config.toUpperCase(),
          installDir.dir(buildConfig).getAsFile().toURI().getPath());
    }
    write(outputStream, 1, "LIBRARY_OUTPUT_DIRECTORY \"%s\"",
        installDir.dir(buildConfig).getAsFile().toURI().getPath());
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, 1, "LIBRARY_OUTPUT_DIRECTORY_%s \"%s\"", config.toUpperCase(),
          installDir.dir(buildConfig).getAsFile().toURI().getPath());
    }
    write(outputStream, 1, "RUNTIME_OUTPUT_DIRECTORY \"%s\"",
        installDir.dir(buildConfig).getAsFile().toURI().getPath());
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, 1, "RUNTIME_OUTPUT_DIRECTORY_%s \"%s\"", config.toUpperCase(),
          installDir.dir(buildConfig).getAsFile().toURI().getPath());
    }
    write(outputStream, ")");
  }

  private void writeStripDebugCommand(final FileOutputStream outputStream, final String target) throws IOException {
    write(outputStream, "add_custom_command( TARGET %s POST_BUILD", target);
    write(outputStream, 1, "COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:%s> $<TARGET_FILE:%s>.debug",
        target, target);
    write(outputStream, 1, "COMMAND ${CMAKE_STRIP} -g $<TARGET_FILE:%s>", target);
    write(outputStream, ")");
  }
}
