/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedExecutable;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectPackageDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedSystemPackage;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeListsFile extends CMakeFileOutputStream {

  private static final String CMAKE_LISTS_FILE = "CMakeLists.txt";
  private static final String CMAKE_MINIMUM_VERSION = "3.21";

  private final Collection<CMakeResolvedToolchain> toolchains;
  private final Project project;

  public CMakeListsFile(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws FileNotFoundException {
    super(project.getLayout().getProjectDirectory().file(CMAKE_LISTS_FILE));
    this.toolchains = toolchains;
    this.project = project;
  }

  @Override
  public void write() throws IOException {
    writeHeader(project);
    writeSystemPackages(toolchains);
    writeProjectPackages(toolchains, project);
    writeLibraries(toolchains, project);
    writeApplications(toolchains, project);
    writeTests(toolchains, project);
  }

  private void writeHeader(final Project project) throws IOException {
    write("cmake_minimum_required( VERSION %s )", CMAKE_MINIMUM_VERSION);
    writeLine();
    write("project( %s LANGUAGES C CXX )", project.getName());
    writeLine();
    write("""
        set( CMAKE_EXPORT_COMPILE_COMMANDS ON CACHE INTERNAL "" )
        set( CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON CACHE INTERNAL "" )
        set( CMAKE_CONFIGURATION_TYPES $ENV{CMAKE_CONFIGURATION_TYPES} )
        set( CMAKE_TOOLCHAIN_FILE $ENV{CMAKE_TOOLCHAIN_FILE} )
        set( CMAKE_BUILD_TYPE $ENV{CMAKE_BUILD_TYPE} )
        set( CMAKE_TOOLCHAIN_NAME $ENV{CMAKE_TOOLCHAIN_NAME} )
        """);
  }

  private void writeSystemPackages(final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getSystemPackages().isEmpty()) {
        write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedSystemPackage resolvedPackage : toolchain.getSystemPackages()) {
          writeLine();
          if (resolvedPackage.getComponents().isEmpty()) {
            write("find_package( %s CONFIG REQUIRED )", resolvedPackage.getName());
          } else {
            write("find_package( %s CONFIG REQUIRED", resolvedPackage.getName());
            for (final String component : resolvedPackage.getComponents()) {
              write(1, "%s::%s", resolvedPackage.getName(), component);
            }
            write(")");
          }
          if (!resolvedPackage.getProperties().isEmpty()) {
            for (final Map.Entry<String, String> property : resolvedPackage.getProperties().entrySet()) {
              write("set( %s_%s %s )", resolvedPackage.getName(), property.getKey().toUpperCase(),
                  property.getValue().toUpperCase());
            }
          }
        }
        write("endif()");
      }
    }
  }

  private void writeProjectPackages(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getProjectPackages().isEmpty()) {
        write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final Project resolvedPackage : toolchain.getProjectPackages()) {
          if (!Objects.equals(project, resolvedPackage)) {
            final String target = CMakeFileConventions.cmakeConfigName(resolvedPackage, toolchain);
            writeLine();
            write("set( %s_DIR \"%s\" )", target, resolvedPackage.getLayout().getBuildDirectory()
                .dir(CMakeFileConventions.CMAKE_BUILD_PATH).get().getAsFile().toURI().getPath());
            write("find_package( %s CONFIG REQUIRED )", target);
          }
        }
        write("endif()");
      }
    }
  }

  private void writeLibraries(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getLibraries().isEmpty()) {
        write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedLibrary object : toolchain.getLibraries()) {
          for (final String buildConfig : toolchain.getBuildConfigs()) {
            if (object.getSources().isEmpty()) {
              writeInterfaceLibrary(object, toolchain, buildConfig, project);
            } else {
              if (object.isBuildStatic()) {
                writeStaticLibrary(object, toolchain, buildConfig, project);
              }
              if (object.isBuildShared()) {
                writeSharedLibrary(object, toolchain, buildConfig, project);
              }
            }
          }
        }
        write("endif()");
      }
    }
  }

  private void writeApplications(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getApplications().isEmpty()) {
        write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedExecutable object : toolchain.getApplications()) {
          for (final String buildConfig : toolchain.getBuildConfigs()) {
            final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, buildConfig);
            writeExecutable(target, object, toolchain, buildConfig, project);
          }
        }
        write("endif()");
      }
    }
  }

  private void writeTests(final Collection<CMakeResolvedToolchain> toolchains, final Project project)
      throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getTests().isEmpty()) {
        writeLine();
        write("enable_testing()");
        write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final CMakeResolvedExecutable object : toolchain.getTests()) {
          for (final String buildConfig : toolchain.getBuildConfigs()) {
            final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, buildConfig);
            writeExecutable(target, object, toolchain, buildConfig, project);
            writeAddTest(target);
          }
        }
        write("endif()");
        write("include( CTest )");
      }
    }
  }

  private void writeInterfaceLibrary(final CMakeResolvedLibrary object, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.INTERFACE,
        buildConfig);
    writeLine();
    write("add_library( %s INTERFACE )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetIncludeDirectories(target, "INTERFACE", object.getHeaders(), project);
    writePublicCompiling(target, object);
    writePublicLinking(target, object, toolchain, buildConfig);
  }

  private void writeStaticLibrary(final CMakeResolvedLibrary object, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.STATIC,
        buildConfig);
    writeLine();
    write("add_library( %s STATIC )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetSources(target, "PUBLIC", object.getSources(), project);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getHeaders(), project);
    writePrivateCompiling(target, object);
    writePublicCompiling(target, object);
    writePrivateLinking(target, object, toolchain, buildConfig);
    writePublicLinking(target, object, toolchain, buildConfig);
    writeOutputTargetProperties(target, object, toolchain, project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, toolchain, project);
    }
  }

  private void writeSharedLibrary(final CMakeResolvedLibrary object, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Project project) throws IOException {
    final String target = CMakeFileConventions.buildTarget(object.getName(), toolchain, CMakeLinkType.SHARED,
        buildConfig);
    writeLine();
    write("add_library( %s SHARED )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getHeaders(), project);
    writeTargetSources(target, "PUBLIC", object.getSources(), project);
    writePrivateCompiling(target, object);
    writePublicCompiling(target, object);
    writePrivateLinking(target, object, toolchain, buildConfig);
    writePublicLinking(target, object, toolchain, buildConfig);
    writeOutputTargetProperties(target, object, toolchain, project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, toolchain, project);
    }
  }

  private void writeExecutable(final String target, final CMakeResolvedBinary<?> object,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Project project) throws IOException {
    writeLine();
    write("add_executable( %s )", target);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getHeaders(), project);
    writeTargetSources(target, "PRIVATE", object.getSources(), project);
    writePrivateCompiling(target, object);
    writePrivateLinking(target, object, toolchain, buildConfig);
    writeOutputTargetProperties(target, object, toolchain, project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, toolchain, project);
    }
  }

  private void writeAddTest(final String target) throws IOException {
    writeLine();
    write("add_test(");
    write(1, "NAME %s", target);
    write(1, "COMMAND $<TARGET_FILE:%s>", target);
    write(")");
  }

  private void writeTargetIncludeDirectories(final String target, final String access, final Collection<String> headers,
      final Project project) throws IOException {
    write("target_include_directories( %s %s", target, access);
    for (final String header : headers) {
      final Directory projectDirectory = project.getLayout().getProjectDirectory();
      final File directory = projectDirectory.dir(header).getAsFile();
      write(1, "%s", projectDirectory.getAsFile().toURI().relativize(directory.toURI()).getPath());
    }
    write(")");
  }

  private void writeTargetSources(final String target, final String access, final Collection<String> sources,
      final Project project) throws IOException {
    write("target_sources( %s %s", target, access);
    for (final String entry : sources) {
      final Directory projectDirectory = project.getLayout().getProjectDirectory();
      for (final File file : projectDirectory.dir(entry).getAsFileTree().getFiles()) {
        write(1, "%s", projectDirectory.getAsFile().toURI().relativize(file.toURI()).getPath());
      }
    }
    write(")");
  }

  private void writeTargetCompileOptions(final String target, final String access, final Collection<String> options)
      throws IOException {
    write("target_compile_options( %s %s", target, access);
    for (final String option : options) {
      write(1, option);
    }
    write(")");
  }

  private void writeTargetCompileDefinitions(final String target, final String type,
      final Collection<String> definitions) throws IOException {
    write("target_compile_definitions( %s %s", target, type);
    for (final String definition : definitions) {
      write(1, definition);
    }
    write(")");
  }

  private void writePrivateCompiling(final String target, final CMakeResolvedBinary<?> binary)
      throws IOException {
    if (!binary.getPrivateCompileOptions().isEmpty()) {
      writeTargetCompileOptions(target, "PRIVATE", binary.getPrivateCompileOptions());
    }
    if (!binary.getPrivateCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(target, "PRIVATE", binary.getPrivateCompileDefinitions());
    }
  }

  private void writePublicCompiling(final String target, final CMakeResolvedLibrary library)
      throws IOException {
    if (!library.getPublicCompileOptions().isEmpty()) {
      writeTargetCompileOptions(target, "PUBLIC", library.getPublicCompileOptions());
    }
    if (!library.getPublicCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(target, "PUBLIC", library.getPublicCompileDefinitions());
    }
  }

  private void writePrivateLinking(final String target, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    if (!binary.getPrivateSystemPackageDependencies().isEmpty()
        || !binary.getPrivateProjectPackageDependencies().isEmpty()
        || !binary.getPrivateLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(target, "PRIVATE", toolchain, buildConfig,
          binary.getPrivateProjectPackageDependencies(),
          binary.getPrivateSystemPackageDependencies(),
          binary.getPrivateLinkOptions());
    }
  }

  private void writePublicLinking(final String target, final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    if (!library.getPublicSystemPackageDependencies().isEmpty()
        || !library.getPublicProjectPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(target, "PUBLIC", toolchain, buildConfig,
          library.getPublicProjectPackageDependencies(),
          library.getPublicSystemPackageDependencies(),
          library.getPublicLinkOptions());
    }
  }

  private void writeTargetLinkLibraries(final String target, final String type, final CMakeResolvedToolchain toolchain,
      final String buildConfig, final Collection<CMakeResolvedProjectPackageDependency> projectPackageDependencies,
      final Collection<String> packageDependencies, final Collection<String> options) throws IOException {
    write("target_link_libraries( %s %s", target, type);
    for (final CMakeResolvedProjectPackageDependency projectModule : projectPackageDependencies) {
      write(1, "%s::%s", projectModule.getProject().getName(),
          CMakeFileConventions.buildTarget(projectModule.getName(), toolchain, projectModule.getType(), buildConfig));
    }
    for (final String packageDependency : packageDependencies) {
      write(1, packageDependency);
    }
    for (final String option : options) {
      write(1, option);
    }
    write(")");
  }

  private void writeOutputTargetProperties(final String target, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final Project project) throws IOException {
    write("set_target_properties( %s PROPERTIES", target);
    write(1, "PREFIX \"\"");
    write(1, "OUTPUT_NAME \"%s\"", target);
    final File installDir = project.getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName())).get().getAsFile();
    write(1, "ARCHIVE_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "ARCHIVE_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(1, "LIBRARY_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "LIBRARY_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(1, "RUNTIME_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "RUNTIME_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(")");
  }

  private void writeStripDebugCommand(final String target, final CMakeResolvedToolchain toolchain,
      final Project project) throws IOException {
    final File installDir = project.getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName()))
        .get().getAsFile();
    write("add_custom_command( TARGET %s POST_BUILD", target);
    write(1, "COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:%s> %s/%s.debug",
        target, installDir.toURI().getPath(), target);
    write(1, "COMMAND ${CMAKE_STRIP} -g $<TARGET_FILE:%s>", target);
    write(")");
  }
}
