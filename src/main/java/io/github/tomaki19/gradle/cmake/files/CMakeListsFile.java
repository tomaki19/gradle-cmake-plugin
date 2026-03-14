/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeListsFile extends CMakeFileContent {

  public static final String NAME = "CMakeLists.txt";

  private static final String CMAKE_MINIMUM_VERSION = "3.21";

  private final Collection<CMakeResolvedToolchain> toolchains;

  public CMakeListsFile(final Collection<CMakeResolvedToolchain> toolchains, final String projectName,
      final Directory projectDirectory, final Directory buildDirectory) {
    super(NAME, projectName, projectDirectory, buildDirectory);
    this.toolchains = toolchains;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    writeHeader(outputStream);
    writeLibraries(outputStream, toolchains);
    writeApplications(outputStream, toolchains);
    writeTests(outputStream, toolchains);
  }

  private void writeHeader(final FileOutputStream outputStream) throws IOException {
    write(outputStream, "cmake_minimum_required( VERSION %s )", CMAKE_MINIMUM_VERSION);
    write(outputStream, "project( %s LANGUAGES C CXX )", getProjectName());
    write(outputStream, "include( GNUInstallDirs )");
    write(outputStream, """
        set( CMAKE_EXPORT_COMPILE_COMMANDS ON CACHE INTERNAL "" )
        set( CMAKE_WINDOWS_EXPORT_ALL_SYMBOLS ON CACHE INTERNAL "" )
        set( CMAKE_TOOLCHAIN_NAME $ENV{CMAKE_TOOLCHAIN_NAME} )
        """);
    write(outputStream, """
        include(CMakePrintHelpers)
        cmake_print_variables(CMAKE_CONFIGURATION_TYPES)
        cmake_print_variables(CMAKE_BUILD_TYPE)
        cmake_print_variables(CMAKE_TOOLCHAIN_FILE)
        cmake_print_variables(CMAKE_TOOLCHAIN_NAME)
        cmake_print_variables(CMAKE_MODULE_PATH)
        cmake_print_variables(CMAKE_PREFIX_PATH)
        cmake_print_variables(CMAKE_FIND_ROOT_PATH)
        cmake_print_variables(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM)
        cmake_print_variables(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY)
        cmake_print_variables(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE)
        cmake_print_variables(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE)
        """);

    write(outputStream,
        """
            function( resolve_real_path target location_list )
                if( TARGET "${target}" )
                    get_target_property( _type ${target} TYPE )
                    if( "${_type}" STREQUAL "SHARED_LIBRARY" )
                        get_target_property( _location ${target} LOCATION )
                        if( WIN32 )
                            # On Windows, the location points to the 'lib' file.
                            get_filename_component( _extension ${_location} EXT )
                            if( "${_extension}" STREQUAL ".lib" )
                                get_filename_component( _directory ${_location} DIRECTORY )
                                get_filename_component( _lib_root ${_directory} DIRECTORY )
                                get_filename_component( _no_ext_path ${_location} NAME_WE )
                                if( EXISTS "${_directory}/${_no_ext_path}.dll" )
                                    list( APPEND location_list "${_directory}/${_no_ext_path}.dll" )
                                elseif( EXISTS "${_lib_root}/bin/${_no_ext_path}.dll" )
                                    list( APPEND location_list "${_lib_root}/bin/${_no_ext_path}.dll" )
                                endif()
                            endif()
                        else()
                            get_filename_component( _realpath ${_location} REALPATH )
                            list( APPEND location_list ${_realpath} )
                        endif()
                    endif()
                endif()
                set( ${location_list} "${${location_list}}" PARENT_SCOPE )
            endfunction()
            function( install_imported_dependency )
                set(options "")
                set( oneValueArgs TARGET COMPONENT )
                set( multiValueArgs "" )
                cmake_parse_arguments( INPUT "${options}" "${oneValueArgs}" "${multiValueArgs}" ${ARGN} )
                set( _link_library_locations "" )
                resolve_real_path( ${INPUT_TARGET} _link_library_locations )
                install( FILES ${_link_library_locations}
                    DESTINATION lib
                    COMPONENT ${INPUT_COMPONENT}
                )
            endfunction()
            function( install_transitive_dependencies )
                set(options "")
                set( oneValueArgs TARGET COMPONENT )
                set( multiValueArgs "" )
                cmake_parse_arguments( INPUT "${options}" "${oneValueArgs}" "${multiValueArgs}" ${ARGN} )
                set( _link_library_locations "" )
                get_target_property( _interface_link_libraries ${INPUT_TARGET} INTERFACE_LINK_LIBRARIES )
                foreach( _interface_link_library ${_interface_link_libraries})
                    resolve_real_path( ${_interface_link_library} _link_library_locations )
                endforeach( _interface_link_library )
                install( FILES ${_link_library_locations}
                    DESTINATION lib
                    COMPONENT ${INPUT_COMPONENT}
                )
            endfunction()
            """);
  }

  private void writeLibraries(final FileOutputStream outputStream,
      final Collection<CMakeResolvedToolchain> toolchains) throws IOException {
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      if (!toolchain.getInterfaceLibraries().isEmpty()
          || !toolchain.getStaticLibraries().isEmpty()
          || !toolchain.getSharedLibraries().isEmpty()) {
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedLibrary component : toolchain.getInterfaceLibraries()) {
            writeTargetPackageDependencies(outputStream, 1, component.getAllPackageDependencies());
            writeTargetProjectDependencies(outputStream, 1, component.getAllProjectDependencies(), toolchain,
                buildConfig);
            writeInterfaceLibrary(outputStream, 1, component, toolchain, buildConfig);
          }
          for (final CMakeResolvedLibrary component : toolchain.getStaticLibraries()) {
            writeTargetPackageDependencies(outputStream, 1, component.getAllPackageDependencies());
            writeTargetProjectDependencies(outputStream, 1, component.getAllProjectDependencies(), toolchain,
                buildConfig);
            writeStaticLibrary(outputStream, 1, component, toolchain, buildConfig);
          }
          for (final CMakeResolvedLibrary component : toolchain.getSharedLibraries()) {
            writeTargetPackageDependencies(outputStream, 1, component.getAllPackageDependencies());
            writeTargetProjectDependencies(outputStream, 1, component.getAllProjectDependencies(), toolchain,
                buildConfig);
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
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable component : toolchain.getApplications()) {
            writeTargetPackageDependencies(outputStream, 1, component.getAllPackageDependencies());
            writeTargetProjectDependencies(outputStream, 1, component.getAllProjectDependencies(), toolchain,
                buildConfig);
            final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain.getName(),
                buildConfig);
            writeExecutable(outputStream, 1, target, component, toolchain, buildConfig);
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
        write(outputStream, "enable_testing()");
        write(outputStream, "include( CTest )");
        write(outputStream, "if( ${CMAKE_TOOLCHAIN_NAME} STREQUAL \"%s\" )", toolchain.getName());
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          for (final CMakeResolvedExecutable component : toolchain.getTests()) {
            writeTargetPackageDependencies(outputStream, 1, component.getAllPackageDependencies());
            writeTargetProjectDependencies(outputStream, 1, component.getAllProjectDependencies(), toolchain,
                buildConfig);
            final String target = CMakeFileConventions.buildTarget(component.getName(), toolchain.getName(),
                buildConfig);
            writeExecutable(outputStream, 1, target, component, toolchain, buildConfig);
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
    final String target = CMakeFileConventions.buildTarget(component.getName(), CMakeLinkVariant.INTERFACE,
        toolchain.getName(), buildConfig);
    write(outputStream, indent, "add_library( %s INTERFACE )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, indent, target, "INTERFACE", component.getHeaders());
    writeTargetPublicCompiling(outputStream, indent, target, component);
    writeTargetPublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    final Collection<CMakeResolvedProjectDependency> projectDependencies = new ArrayList<>();
    projectDependencies.addAll(component.getPrivateProjectDependencies());
    projectDependencies.addAll(component.getPublicProjectDependencies());
    writeTargetInstall(outputStream, indent, target, toolchain, buildConfig, projectDependencies);
  }

  private void writeStaticLibrary(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    final String target = CMakeFileConventions.buildTarget(component.getName(), CMakeLinkVariant.STATIC,
        toolchain.getName(), buildConfig);
    final String outputName = component.getOutputName();
    write(outputStream, indent, "add_library( %s STATIC )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, indent, target, "PUBLIC", component.getHeaders());
    writeTargetSources(outputStream, indent, target, component.getSources());
    writeTargetPrivateCompiling(outputStream, indent, target, component);
    writeTargetPublicCompiling(outputStream, indent, target, component);
    writeTargetPrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetPublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetProperties(outputStream, indent, target, outputName, toolchain, buildConfig);
    writeTargetInstall(outputStream, indent, target, toolchain, buildConfig, component.getAllProjectDependencies());
    if (component.isStripDebug()) {
      writeStripDebugCommand(outputStream, indent, target);
    }
  }

  private void writeSharedLibrary(final FileOutputStream outputStream, final int indent,
      final CMakeResolvedLibrary component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    final String target = CMakeFileConventions.buildTarget(component.getName(), CMakeLinkVariant.SHARED,
        toolchain.getName(), buildConfig);
    write(outputStream, indent, "add_library( %s SHARED )", target);
    write(outputStream, indent, "add_library( %s::%s ALIAS %s)", getProjectName(), target, target);
    writeTargetIncludeDirectories(outputStream, indent, target, "PUBLIC", component.getHeaders());
    writeTargetSources(outputStream, indent, target, component.getSources());
    writeTargetPrivateCompiling(outputStream, indent, target, component);
    writeTargetPublicCompiling(outputStream, indent, target, component);
    writeTargetPrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetPublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetProperties(outputStream, indent, target, component.getOutputName(), toolchain,
        buildConfig);
    writeTargetInstall(outputStream, indent, target, toolchain, buildConfig, component.getAllProjectDependencies());
    if (component.isStripDebug()) {
      writeStripDebugCommand(outputStream, indent, target);
    }
  }

  private void writeExecutable(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> component, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    write(outputStream, indent, "add_executable( %s )", target);
    writeTargetIncludeDirectories(outputStream, indent, target, "PRIVATE", component.getHeaders());
    writeTargetSources(outputStream, indent, target, component.getSources());
    writeTargetPrivateCompiling(outputStream, indent, target, component);
    writeTargetPublicCompiling(outputStream, indent, target, component);
    writeTargetPrivateLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetPublicLinking(outputStream, indent, target, component, toolchain, buildConfig);
    writeTargetProperties(outputStream, indent, target, component.getOutputName(), toolchain,
        buildConfig);
    writeTargetInstall(outputStream, indent, target, toolchain, buildConfig,
        component.getPrivateProjectDependencies());
    if (component.isStripDebug()) {
      writeStripDebugCommand(outputStream, 1, target);
    }
  }

  private void writeAddTest(final FileOutputStream outputStream, final int indent, final String target,
      final String outputName) throws IOException {
    write(outputStream, indent, "add_test(");
    write(outputStream, indent + 1, "NAME %s", target);
    write(outputStream, indent + 1, "COMMAND $<TARGET_FILE:%s>", target);
    write(outputStream, indent, ")");
  }

  private void writeTargetPackageDependencies(final FileOutputStream outputStream, final int indent,
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

  private void writeTargetProjectDependencies(final FileOutputStream outputStream, final int indent,
      final Collection<CMakeResolvedProjectDependency> dependencies, final CMakeResolvedToolchain toolchain,
      final String buildConfig) throws IOException {
    for (final CMakeResolvedProjectDependency dependency : dependencies) {
      if (!Objects.equals(getProjectName(), dependency.getProjectName())) {
        write(outputStream, indent, "include( %s )", CMakeFileConventions.moduleTarget(dependency.getProjectName(),
            dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig));
      }
    }
  }

  private void writeTargetIncludeDirectories(final FileOutputStream outputStream, final int indent, final String target,
      final String access, final Collection<File> headers) throws IOException {
    write(outputStream, indent, "target_include_directories( %s %s", target, access);
    final File workingDir = getProjectDirectory().getAsFile();
    for (final File headerDir : headers) {
      write(outputStream, indent + 1, "\"$<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/%s>\"",
          workingDir.toPath().relativize(headerDir.toPath()));
    }
    write(outputStream, indent + 1, "\"$<INSTALL_INTERFACE:${CMAKE_INSTALL_INCLUDEDIR}>\"");
    write(outputStream, indent, ")");
  }

  private void writeTargetSources(final FileOutputStream outputStream, final int indent, final String target,
      final Collection<File> sources) throws IOException {
    write(outputStream, indent, "target_sources( %s PRIVATE", target);
    final Path workingDir = getProjectDirectory().getAsFile().toPath();
    for (final File sourceFile : sources) {
      write(outputStream, indent + 1, "\"${CMAKE_CURRENT_SOURCE_DIR}/%s\"", workingDir.relativize(sourceFile.toPath()));
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetCompileOptions(final FileOutputStream outputStream, final int indent, final String target,
      final String access, final Collection<String> options) throws IOException {
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

  private void writeTargetPrivateCompiling(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary) throws IOException {
    if (!binary.getPrivateCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, indent, target, "PRIVATE", binary.getPrivateCompileOptions());
    }
    if (!binary.getPrivateCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, indent, target, "PRIVATE", binary.getPrivateCompileDefinitions());
    }
  }

  private void writeTargetPublicCompiling(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary) throws IOException {
    if (!binary.getPublicCompileOptions().isEmpty()) {
      writeTargetCompileOptions(outputStream, indent, target, "PUBLIC", binary.getPublicCompileOptions());
    }
    if (!binary.getPublicCompileDefinitions().isEmpty()) {
      writeTargetCompileDefinitions(outputStream, indent, target, "PUBLIC", binary.getPublicCompileDefinitions());
    }
  }

  private void writeTargetPrivateLinking(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    if (!binary.getPrivateProjectDependencies().isEmpty() ||
        !binary.getPrivatePackageDependencies().isEmpty() ||
        !binary.getPrivateLinkOptions().isEmpty()) {
      writeTargetLinkLibraries(outputStream, indent, target, "PRIVATE", toolchain, buildConfig,
          binary.getPrivateLinkOptions(),
          binary.getPrivateProjectDependencies(),
          binary.getPrivatePackageDependencies());
    }
  }

  private void writeTargetPublicLinking(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedBinary<?> binary, final CMakeResolvedToolchain toolchain, final String buildConfig)
      throws IOException {
    System.out.println("Public linking for target " + target);
    if (!binary.getPublicProjectDependencies().isEmpty() ||
        !binary.getPublicPackageDependencies().isEmpty() ||
        !binary.getPublicLinkOptions().isEmpty()) {
      System.out.println("Target has linking " + target);
      writeTargetLinkLibraries(outputStream, indent, target, "PUBLIC", toolchain, buildConfig,
          binary.getPublicLinkOptions(),
          binary.getPublicProjectDependencies(),
          binary.getPublicPackageDependencies());
    }
  }

  private void writeTargetLinkLibraries(final FileOutputStream outputStream, final int indent, final String target,
      final String access, final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<String> options, final Collection<CMakeResolvedProjectDependency> projectDependencies,
      final Collection<CMakeResolvedPackageDependency> packageDependencies)
      throws IOException {
    write(outputStream, indent, "target_link_libraries( %s %s", target, access);
    for (final String option : options) {
      write(outputStream, indent + 1, option);
    }
    for (final CMakeResolvedProjectDependency projectModule : projectDependencies) {
      write(outputStream, indent + 1, "%s", CMakeFileConventions.buildTarget(projectModule.getProjectName(),
          projectModule.getName(), projectModule.getLinkType(), toolchain.getName(), buildConfig));
    }
    for (final CMakeResolvedPackageDependency packageDependency : packageDependencies) {
      write(outputStream, indent + 1, "%s::%s", packageDependency.getTargetPrefix(), packageDependency.getName());
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetProperties(final FileOutputStream outputStream, final int indent, final String target,
      final String outputName, final CMakeResolvedToolchain toolchain, final String buildConfig) throws IOException {
    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    final Path configPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH).dir(toolchain.getName())
        .dir(buildConfig).getAsFile().toPath();
    write(outputStream, indent, "set_target_properties( %s PROPERTIES", target);
    write(outputStream, indent + 1, "OUTPUT_NAME \"%s\"", outputName);
    write(outputStream, indent + 1, "ARCHIVE_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s/bin\"",
        projectPath.relativize(configPath));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "ARCHIVE_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s/bin\"",
          config.toUpperCase(), projectPath.relativize(configPath));
    }
    write(outputStream, indent + 1, "LIBRARY_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s/lib\"",
        projectPath.relativize(configPath));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "LIBRARY_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s/lib\"",
          config.toUpperCase(), projectPath.relativize(configPath));
    }
    write(outputStream, indent + 1, "RUNTIME_OUTPUT_DIRECTORY \"${CMAKE_CURRENT_SOURCE_DIR}/%s/bin\"",
        projectPath.relativize(configPath));
    for (final String config : toolchain.getBuildConfigs()) {
      write(outputStream, indent + 1, "RUNTIME_OUTPUT_DIRECTORY_%s \"${CMAKE_CURRENT_SOURCE_DIR}/%s/bin\"",
          config.toUpperCase(), projectPath.relativize(configPath));
    }
    write(outputStream, indent, ")");
  }

  private void writeTargetInstall(final FileOutputStream outputStream, final int indent, final String target,
      final CMakeResolvedToolchain toolchain, final String buildConfig,
      final Collection<CMakeResolvedProjectDependency> dependencies) throws IOException {
    write(outputStream, indent, "install( TARGETS %s", target);
    write(outputStream, indent + 1, "COMPONENT %s", target);
    write(outputStream, indent, ")");
    for (final CMakeResolvedProjectDependency dependency : dependencies) {
      if (Objects.equals(getProjectName(), dependency.getProjectName())
          && Objects.equals(CMakeLinkVariant.SHARED, dependency.getLinkType())) {
        final String buildTarget = CMakeFileConventions.buildTarget(
            dependency.getName(), dependency.getLinkType(), toolchain.getName(), buildConfig);
        write(outputStream, indent, "install( TARGETS %s", buildTarget);
        write(outputStream, indent + 1, "COMPONENT %s", target);
        write(outputStream, indent, ")");
        write(outputStream, indent, "install_transitive_dependencies( TARGET %s", buildTarget);
        write(outputStream, indent + 1, "COMPONENT %s", target);
        write(outputStream, indent, ")");
      } else {
        final String buildTarget = CMakeFileConventions.buildTarget(dependency.getProjectName(), dependency.getName(),
            dependency.getLinkType(), toolchain.getName(), buildConfig);
        write(outputStream, indent, "install_imported_dependency( TARGET %s", buildTarget);
        write(outputStream, indent + 1, "COMPONENT %s", target);
        write(outputStream, indent, ")");
        write(outputStream, indent, "install_transitive_dependencies( TARGET %s", buildTarget);
        write(outputStream, indent + 1, "COMPONENT %s", target);
        write(outputStream, indent, ")");
      }
    }
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
