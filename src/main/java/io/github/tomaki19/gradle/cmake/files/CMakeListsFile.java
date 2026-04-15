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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Project;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeListsFile extends CMakeFileContent {

  public static final String NAME = "CMakeLists.txt";

  private final Collection<CMakeResolvedToolchain> toolchains;

  public CMakeListsFile(final Collection<CMakeResolvedToolchain> toolchains, final Project project) {
    super(NAME, project);
    this.toolchains = toolchains;
  }

  @Override
  public void writeTo(final FileOutputStream outputStream) throws IOException {
    processTemplate("lists-file.ftl", buildModel(), outputStream);
  }

  private Map<String, Object> buildModel() {
    final Map<String, Object> model = new HashMap<>();
    model.put("projectName", getProjectName());

    final List<Map<String, Object>> toolchainModels = new ArrayList<>();
    for (final CMakeResolvedToolchain toolchain : toolchains) {
      toolchainModels.add(buildToolchainModel(toolchain));
    }
    model.put("toolchains", toolchainModels);
    return model;
  }

  private Map<String, Object> buildToolchainModel(final CMakeResolvedToolchain toolchain) {
    final Map<String, Object> model = new HashMap<>();
    model.put("name", toolchain.getName());

    final boolean hasLibraries = !toolchain.getInterfaceLibraries().isEmpty()
        || !toolchain.getStaticLibraries().isEmpty()
        || !toolchain.getSharedLibraries().isEmpty();
    model.put("hasLibraries", hasLibraries);
    model.put("hasApplications", !toolchain.getApplications().isEmpty());
    model.put("hasTests", !toolchain.getTests().isEmpty());

    final List<Map<String, Object>> interfaceLibs = new ArrayList<>();
    final List<Map<String, Object>> staticLibs = new ArrayList<>();
    final List<Map<String, Object>> sharedLibs = new ArrayList<>();
    final List<Map<String, Object>> applications = new ArrayList<>();
    final List<Map<String, Object>> tests = new ArrayList<>();

    for (final String buildConfig : toolchain.getBuildConfigs()) {
      for (final CMakeResolvedLibrary lib : toolchain.getInterfaceLibraries()) {
        interfaceLibs.add(buildInterfaceLibraryModel(lib, toolchain, buildConfig));
      }
      for (final CMakeResolvedLibrary lib : toolchain.getStaticLibraries()) {
        staticLibs.add(buildBinaryLibraryModel(lib, toolchain, buildConfig));
      }
      for (final CMakeResolvedLibrary lib : toolchain.getSharedLibraries()) {
        sharedLibs.add(buildBinaryLibraryModel(lib, toolchain, buildConfig));
      }
      for (final CMakeResolvedExecutable exec : toolchain.getApplications()) {
        applications.add(buildExecutableModel(exec, toolchain, buildConfig));
      }
      for (final CMakeResolvedExecutable exec : toolchain.getTests()) {
        tests.add(buildExecutableModel(exec, toolchain, buildConfig));
      }
    }

    model.put("interfaceLibraries", interfaceLibs);
    model.put("staticLibraries", staticLibs);
    model.put("sharedLibraries", sharedLibs);
    model.put("applications", applications);
    model.put("tests", tests);
    return model;
  }

  private Map<String, Object> buildInterfaceLibraryModel(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(library, toolchain, buildConfig);
    model.put("target", target);
    model.put("projectAliasTarget", "%s::%s".formatted(getProjectName(), target));
    model.put("packageDependencies", library.getAllPackageDependencies());
    model.put("projectIncludes",
        buildFilteredProjectIncludes(library.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(library.getHeaders(), projectPath));

    model.put("hasInterfaceCompileOptions", !library.getPublicCompileOptions().isEmpty());
    model.put("interfaceCompileOptions", library.getPublicCompileOptions());
    model.put("hasInterfaceCompileDefinitions", !library.getPublicCompileDefinitions().isEmpty());
    model.put("interfaceCompileDefinitions", library.getPublicCompileDefinitions());

    final boolean hasInterfaceLinking = !library.getPublicProjectDependencies().isEmpty()
        || !library.getPublicPackageDependencies().isEmpty()
        || !library.getPublicLinkOptions().isEmpty();
    model.put("hasInterfaceLinking", hasInterfaceLinking);
    model.put("interfaceLinkLibraries", buildLinkLibraries(library.getPublicLinkOptions(),
        library.getPublicProjectDependencies(), library.getPublicPackageDependencies(), toolchain, buildConfig));

    return model;
  }

  private Map<String, Object> buildBinaryLibraryModel(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(library, toolchain, buildConfig);
    model.put("target", target);
    model.put("projectAliasTarget", "%s::%s".formatted(getProjectName(), target));
    model.put("packageDependencies", library.getAllPackageDependencies());
    model.put("projectIncludes",
        buildFilteredProjectIncludes(library.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(library.getHeaders(), projectPath));
    model.put("sourcePaths", buildRelativeFilePaths(library.getSources(), projectPath));

    populateCompileModel(model, library);
    populateLinkModel(model, library, toolchain, buildConfig, false);

    model.put("outputName", library.getOutputName());

    final Path targetPath = CMakeFileConventions.targetBinaryDirectory(getBuildDirectoryProperty(), library,
        toolchain, buildConfig).getAsFile().toPath();
    model.put("targetRelPath", projectPath.relativize(targetPath));
    model.put("buildConfigs", toolchain.getBuildConfigs());
    model.put("stripDebug", library.isStripDebug());
    return model;
  }

  private Map<String, Object> buildExecutableModel(final CMakeResolvedExecutable executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(executable, toolchain, buildConfig);
    model.put("target", target);
    model.put("packageDependencies", executable.getAllPackageDependencies());
    model.put("projectIncludes",
        buildFilteredProjectIncludes(executable.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(executable.getHeaders(), projectPath));
    model.put("sourcePaths", buildRelativeFilePaths(executable.getSources(), projectPath));

    populateCompileModel(model, executable);
    populateLinkModel(model, executable, toolchain, buildConfig, false);

    model.put("outputName", executable.getOutputName());

    final Path targetPath = CMakeFileConventions.targetBinaryDirectory(getBuildDirectoryProperty(), executable,
        toolchain, buildConfig).getAsFile().toPath();
    model.put("targetRelPath", projectPath.relativize(targetPath));
    model.put("buildConfigs", toolchain.getBuildConfigs());
    model.put("stripDebug", executable.isStripDebug());
    return model;
  }

  private void populateCompileModel(final Map<String, Object> model, final CMakeResolvedBinary<?> binary) {
    model.put("hasPrivateCompileOptions", !binary.getPrivateCompileOptions().isEmpty());
    model.put("privateCompileOptions", binary.getPrivateCompileOptions());
    model.put("hasPublicCompileOptions", !binary.getPublicCompileOptions().isEmpty());
    model.put("publicCompileOptions", binary.getPublicCompileOptions());
    model.put("hasPrivateCompileDefinitions", !binary.getPrivateCompileDefinitions().isEmpty());
    model.put("privateCompileDefinitions", binary.getPrivateCompileDefinitions());
    model.put("hasPublicCompileDefinitions", !binary.getPublicCompileDefinitions().isEmpty());
    model.put("publicCompileDefinitions", binary.getPublicCompileDefinitions());
  }

  private void populateLinkModel(final Map<String, Object> model, final CMakeResolvedBinary<?> binary,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final boolean interfaceType) {
    final boolean hasPrivateLinking = !binary.getPrivateProjectDependencies().isEmpty()
        || !binary.getPrivatePackageDependencies().isEmpty()
        || !binary.getPrivateLinkOptions().isEmpty();
    model.put("hasPrivateLinking", hasPrivateLinking);
    model.put("privateLinkLibraries", buildLinkLibraries(binary.getPrivateLinkOptions(),
        binary.getPrivateProjectDependencies(), binary.getPrivatePackageDependencies(), toolchain, buildConfig));

    final boolean hasPublicLinking = !binary.getPublicProjectDependencies().isEmpty()
        || !binary.getPublicPackageDependencies().isEmpty()
        || !binary.getPublicLinkOptions().isEmpty();
    model.put("hasPublicLinking", hasPublicLinking);
    model.put("publicLinkLibraries", buildLinkLibraries(binary.getPublicLinkOptions(),
        binary.getPublicProjectDependencies(), binary.getPublicPackageDependencies(), toolchain, buildConfig));
  }

  private List<String> buildFilteredProjectIncludes(final Collection<CMakeResolvedProjectDependency> dependencies,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final List<String> includes = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dependency : dependencies) {
      if (!Objects.equals(getProjectName(), dependency.getProjectName())) {
        includes.add(CMakeFileConventions.moduleTarget(dependency, toolchain, buildConfig));
      }
    }
    return includes;
  }

  private List<Path> buildRelativePaths(final Collection<File> directories, final Path base) {
    final List<Path> paths = new ArrayList<>();
    for (final File directory : directories) {
      paths.add(base.relativize(directory.toPath()));
    }
    return paths;
  }

  private List<Path> buildRelativeFilePaths(final Collection<File> files, final Path base) {
    final List<Path> paths = new ArrayList<>();
    for (final File file : files) {
      paths.add(base.relativize(file.toPath()));
    }
    return paths;
  }

  private List<String> buildLinkLibraries(final Collection<String> options,
      final Collection<CMakeResolvedProjectDependency> projectDependencies,
      final Collection<CMakeResolvedPackageDependency> packageDependencies, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final List<String> libs = new ArrayList<>();
    for (final String option : options) {
      libs.add(option);
    }
    for (final CMakeResolvedProjectDependency dependency : projectDependencies) {
      libs.add("%s::%s".formatted(dependency.getProjectName(),
          CMakeFileConventions.buildTarget(dependency, toolchain, buildConfig)));
    }
    for (final CMakeResolvedPackageDependency dependency : packageDependencies) {
      libs.add(dependency.getTargetPrefix() + "::" + dependency.getName());
    }
    return libs;
  }

}
