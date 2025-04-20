/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import ch.tomaki.gradle.cmake.model.CMakeResolvedApplication;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolvedFindPackage;
import ch.tomaki.gradle.cmake.model.CMakeResolvedFindPackageDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedInterface;
import ch.tomaki.gradle.cmake.model.CMakeResolvedLibrary;
import ch.tomaki.gradle.cmake.model.CMakeResolvedProjectModuleDependency;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeListsFile extends CMakeFileOutputStream {

  public static final String FILE_NAME = "CMakeLists.txt";

  private static final String CMAKE_MINIMUM_VERSION = "3.21";

  public CMakeListsFile(final Project project) throws FileNotFoundException {
    super(project.getLayout().getProjectDirectory().file(FILE_NAME));
  }

  @Override
  public void write(final CMakeResolvedBuild build, final Project project) throws IOException {
    writeHeader(project);
    writeFindPackages(build.getFindPackages());
    writeProjectDependencies(build.getProjectModuleDependencies(), project);
    writeInterfaces(build.getInterfaces(), project);
    writeLibraries(build.getLibraries(), project);
    writeApplications(build.getApplications(), project);
    writeTests(build.getTests(), project);
  }

  private void writeHeader(final Project project) throws IOException {
    write("cmake_minimum_required( VERSION %s )", CMAKE_MINIMUM_VERSION);
    writeLine();
    write("project( %s LANGUAGES C CXX )", project.getName());
    writeLine();
    write("""
        set( CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS TRUE )
        set( CMAKE_CONFIGURATION_TYPES $ENV{CMAKE_CONFIGURATION_TYPES} )
        set( CMAKE_TOOLCHAIN_FILE $ENV{CMAKE_TOOLCHAIN_FILE} )
        set( CMAKE_BUILD_TYPE $ENV{CMAKE_BUILD_TYPE} )
        set( CMAKE_TOOLCHAIN_NAME $ENV{CMAKE_TOOLCHAIN_NAME} )

        include(CMakePrintHelpers)
        cmake_print_variables(CMAKE_TOOLCHAIN_NAME)
        """);
  }

  private void writeFindPackages(final Set<CMakeResolvedFindPackage> findPackages) throws IOException {
    for (final CMakeResolvedFindPackage object : findPackages) {
      writeLine();
      write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", object.getToolchain().getName());
      if (object.getComponents().isEmpty()) {
        write("find_package( %s CONFIG REQUIRED )", object.getName());
      } else {
        write("find_package( %s CONFIG REQUIRED", object.getName());
        for (final String component : object.getComponents()) {
          write(1, "%s::%s", object.getName(), component);
        }
        write(")");
      }
      if (!object.getProperties().isEmpty()) {
        for (final Map.Entry<String, String> property : object.getProperties().entrySet()) {
          write("set( %s_%s %s )", object.getName(), property.getKey().toUpperCase(),
              property.getValue().toUpperCase());
        }
      }
      write("endif()");
    }
  }

  private void writeProjectDependencies(final Set<CMakeResolvedProjectModuleDependency> projects, final Project project)
      throws IOException {
    final Set<String> processed = new HashSet<>();
    for (final CMakeResolvedProjectModuleDependency object : projects) {
      if (!processed.contains(object.getProjectName())) {
        writeLine();
        if (!Objects.equals(project.getName(), object.getProjectName())) {
          write("set( %s_DIR \"%s\" )", object.getProjectName(),
              object.getInstallDirectory().getAsFile().toURI().getPath());
          write("find_package( %s REQUIRED )", object.getProjectName());
        }
        processed.add(object.getProjectName());
      }
    }
  }

  private void writeInterfaces(final Set<CMakeResolvedInterface> interfaces, final Project project) throws IOException {
    for (final CMakeResolvedInterface object : interfaces) {
      writeLine();
      writeInterfaceLibrary(object, project);
    }
  }

  private void writeLibraries(final Set<CMakeResolvedLibrary> libraries, final Project project)
      throws IOException {
    for (final CMakeResolvedLibrary object : libraries) {
      writeLine();
      if (object.isBuildStatic()) {
        writeStaticLibrary(object, project);
      }
      if (object.isBuildShared()) {
        writeSharedLibrary(object, project);
      }
    }
  }

  private void writeApplications(final Set<CMakeResolvedApplication> applications, final Project project)
      throws IOException {
    for (final CMakeResolvedApplication object : applications) {
      final String target = CMakeListsConventions.applicationTarget(object.getName(), object.getToolchain(),
          object.getBuildConfig());
      writeLine();
      writeExecutable(target, object, project);
    }
  }

  private void writeTests(final Set<CMakeResolvedTest> tests, final Project project)
      throws IOException {
    if (!tests.isEmpty()) {
      writeLine();
      write("enable_testing()");
      for (final CMakeResolvedTest object : tests) {
        final String target = CMakeListsConventions.testTarget(object.getName(), object.getToolchain(),
            object.getBuildConfig());
        writeLine();
        writeExecutable(target, object, project);
        writeAddTest(target, object);
      }
    }
  }

  private void writeInterfaceLibrary(final CMakeResolvedInterface object, final Project project)
      throws IOException {
    final String target = CMakeListsConventions.interfaceLibraryTarget(object.getName());
    write("add_library( %s INTERFACE )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetIncludeDirectories(target, "INTERFACE", object.getIncludes(), project);
  }

  private void writeStaticLibrary(final CMakeResolvedLibrary object, final Project project)
      throws IOException {
    final String target = CMakeListsConventions.staticLibraryTarget(
        object.getName(), object.getToolchain(), object.getBuildConfig());
    write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", object.getToolchain().getName());
    write("add_library( %s STATIC )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetSources(target, "PUBLIC", object.getSources(), project);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getIncludes(), project);
    writePrivateCompiling(target, object);
    writePublicCompiling(target, object);
    writePrivateLinking(target, object);
    writePublicLinking(target, object);
    writeOutputTargetProperties(target, object.getToolchain(), project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, object.getToolchain(), project);
    }
    write("endif()");
  }

  private void writeSharedLibrary(final CMakeResolvedLibrary object, final Project project)
      throws IOException {
    final String target = CMakeListsConventions.sharedLibraryTarget(object.getName(), object.getToolchain(),
        object.getBuildConfig());
    write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", object.getToolchain().getName());
    write("add_library( %s SHARED )", target);
    write("add_library( %s::%s ALIAS %s)", project.getName(), target, target);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getIncludes(), project);
    writeTargetSources(target, "PUBLIC", object.getSources(), project);
    writePrivateCompiling(target, object);
    writePublicCompiling(target, object);
    writePrivateLinking(target, object);
    writePublicLinking(target, object);
    writeOutputTargetProperties(target, object.getToolchain(), project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, object.getToolchain(), project);
    }
    write("endif()");
  }

  private void writeExecutable(final String target, final CMakeResolvedBinary object, final Project project)
      throws IOException {
    write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", object.getToolchain().getName());
    write("add_executable( %s )", target);
    writeTargetIncludeDirectories(target, "PUBLIC", object.getIncludes(), project);
    writeTargetSources(target, "PRIVATE", object.getSources(), project);
    writePrivateCompiling(target, object);
    writePrivateLinking(target, object);
    writeOutputTargetProperties(target, object.getToolchain(), project);
    if (object.isStripDebug()) {
      writeStripDebugCommand(target, object.getToolchain(), project);
    }
    write("endif()");
  }

  private void writeAddTest(final String target, final CMakeResolvedBinary object) throws IOException {
    write("if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", object.getToolchain().getName());
    write("add_test(");
    write(1, "NAME %s", target);
    write(1, "COMMAND $<TARGET_FILE:%s>", target);
    write(")");
    write("endif()");
  }

  private void writeTargetIncludeDirectories(final String target, final String access, final Set<String> includes,
      final Project project) throws IOException {
    write("target_include_directories( %s %s", target, access);
    for (final String include : includes) {
      final Directory projectDirectory = project.getLayout().getProjectDirectory();
      final File directory = projectDirectory.dir(include).getAsFile();
      write(1, "%s", projectDirectory.getAsFile().toURI().relativize(directory.toURI()).getPath());
    }
    write(")");
  }

  private void writeTargetSources(final String target, final String access, final Set<String> sources,
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

  private void writeTargetCompileOptions(final String target, final String access, final Set<String> options)
      throws IOException {
    write("target_compile_options( %s %s", target, access);
    for (final String option : options) {
      write(1, option);
    }
    write(")");
  }

  private void writeTargetCompileDefinitions(final String target, final String type, final Set<String> definitions)
      throws IOException {
    write("target_compile_definitions( %s %s", target, type);
    for (final String definition : definitions) {
      write(1, definition);
    }
    write(")");
  }

  private void writePrivateCompiling(final String target, final CMakeResolvedBinary binary)
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

  private void writePrivateLinking(final String target, final CMakeResolvedBinary binary)
      throws IOException {
    if (!binary.getPrivateFindPackageDependencies().isEmpty()
        || !binary.getPrivateProjectModuleDependencies().isEmpty()
        || !binary.getPrivateLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(
          target,
          "PRIVATE",
          binary.getPrivateProjectModuleDependencies(),
          binary.getPrivateFindPackageDependencies(),
          binary.getPrivateLinkOptions());
    }
  }

  private void writePublicLinking(final String target, final CMakeResolvedLibrary library)
      throws IOException {
    if (!library.getPublicFindPackageDependencies().isEmpty()
        || !library.getPublicProjectModuleDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(
          target,
          "PUBLIC",
          library.getPublicProjectModuleDependencies(),
          library.getPublicFindPackageDependencies(),
          library.getPublicLinkOptions());
    }
  }

  private void writeTargetLinkLibraries(final String target, final String type,
      final Set<CMakeResolvedProjectModuleDependency> projectModules,
      final Set<CMakeResolvedFindPackageDependency> findPackages,
      final Set<String> options) throws IOException {
    write("target_link_libraries( %s %s", target, type);
    for (final CMakeResolvedProjectModuleDependency projectModule : projectModules) {
      write(1, projectModule.getBuildTarget());
    }
    for (final CMakeResolvedFindPackageDependency findPackage : findPackages) {
      write(1, findPackage.getIdentifier());
    }
    for (final String option : options) {
      write(1, option);
    }
    write(")");
  }

  private void writeOutputTargetProperties(final String target, final CMakeResolvedToolchain toolchain,
      final Project project) throws IOException {
    write("set_target_properties( %s PROPERTIES", target);
    write(1, "PREFIX \"\"");
    write(1, "OUTPUT_NAME \"%s\"", target);
    final File installDir = project.getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_INSTALL_PATH, toolchain.getName()))
        .get().getAsFile();
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "ARCHIVE_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(1, "ARCHIVE_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "LIBRARY_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(1, "LIBRARY_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    for (final String buildConfig : toolchain.getBuildConfigs()) {
      write(1, "RUNTIME_OUTPUT_DIRECTORY_%s \"%s\"",
          buildConfig.toUpperCase(), installDir.toURI().getPath());
    }
    write(1, "RUNTIME_OUTPUT_DIRECTORY \"%s\"", installDir.toURI().getPath());
    write(")");
  }

  private void writeStripDebugCommand(final String target, final CMakeResolvedToolchain toolchain,
      final Project project) throws IOException {
    final File installDir = project.getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_INSTALL_PATH, toolchain.getName()))
        .get().getAsFile();
    write("add_custom_command( TARGET %s POST_BUILD", target);
    write(1, "COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:%s> %s/%s.debug",
        target, installDir.toURI().getPath(), target);
    write(1, "COMMAND ${CMAKE_STRIP} -g $<TARGET_FILE:%s>", target);
    write(")");
  }
}
