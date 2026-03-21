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

import org.gradle.api.file.Directory;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackageDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public final class CMakeListsFile extends CMakeFileContent {

  public static final String NAME = "CMakeLists.txt";

  private final Collection<CMakeResolvedToolchain> toolchains;

  public CMakeListsFile(final Collection<CMakeResolvedToolchain> toolchains, final String projectName,
      final Directory projectDirectory, final Directory buildDirectory) {
    super(NAME, projectName, projectDirectory, buildDirectory);
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
        staticLibs.add(buildBinaryLibraryModel(lib, toolchain, buildConfig, CMakeLinkVariant.STATIC));
      }
      for (final CMakeResolvedLibrary lib : toolchain.getSharedLibraries()) {
        sharedLibs.add(buildBinaryLibraryModel(lib, toolchain, buildConfig, CMakeLinkVariant.SHARED));
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

  private Map<String, Object> buildInterfaceLibraryModel(final CMakeResolvedLibrary lib,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(lib.getName(), CMakeLinkVariant.INTERFACE,
        toolchain.getName(), buildConfig);
    model.put("target", target);
    model.put("projectAliasTarget", "%s::%s".formatted(getProjectName(), target));
    model.put("packageDependencies", lib.getAllPackageDependencies());
    model.put("projectIncludes", buildFilteredProjectIncludes(lib.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(lib.getHeaders(), projectPath));

    model.put("hasInterfaceCompileOptions", !lib.getPublicCompileOptions().isEmpty());
    model.put("interfaceCompileOptions", lib.getPublicCompileOptions());
    model.put("hasInterfaceCompileDefinitions", !lib.getPublicCompileDefinitions().isEmpty());
    model.put("interfaceCompileDefinitions", lib.getPublicCompileDefinitions());

    final boolean hasInterfaceLinking = !lib.getPublicProjectDependencies().isEmpty()
        || !lib.getPublicPackageDependencies().isEmpty()
        || !lib.getPublicLinkOptions().isEmpty();
    model.put("hasInterfaceLinking", hasInterfaceLinking);
    model.put("interfaceLinkLibraries", buildLinkLibraries(lib.getPublicLinkOptions(),
        lib.getPublicProjectDependencies(), lib.getPublicPackageDependencies(), toolchain, buildConfig));

    final Path installConfigPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH)
        .dir(toolchain.getName()).dir(buildConfig).dir(target).dir("lib").getAsFile().toPath();
    model.put("installDir", projectPath.relativize(installConfigPath).toString());
    model.put("installDependencies",
        buildInstallDependencies(lib.getAllProjectDependencies(), toolchain, buildConfig, projectPath,
            installConfigPath, target));
    return model;
  }

  private Map<String, Object> buildBinaryLibraryModel(final CMakeResolvedLibrary lib,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final CMakeLinkVariant linkVariant) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(lib.getName(), linkVariant, toolchain.getName(), buildConfig);
    model.put("target", target);
    model.put("projectAliasTarget", "%s::%s".formatted(getProjectName(), target));
    model.put("packageDependencies", lib.getAllPackageDependencies());
    model.put("projectIncludes", buildFilteredProjectIncludes(lib.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(lib.getHeaders(), projectPath));
    model.put("sourcePaths", buildRelativeFilePaths(lib.getSources(), projectPath));

    populateCompileModel(model, lib);
    populateLinkModel(model, lib, toolchain, buildConfig, false);

    model.put("outputName", lib.getOutputName());

    final Path configPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH)
        .dir(toolchain.getName()).dir(buildConfig).dir(target).getAsFile().toPath();
    model.put("configRelPath", projectPath.relativize(configPath).toString());
    model.put("buildConfigs", toolchain.getBuildConfigs());

    final Path installConfigPath = configPath.resolve("lib");
    model.put("installDir", projectPath.relativize(installConfigPath).toString());
    model.put("installDependencies",
        buildInstallDependencies(lib.getAllProjectDependencies(), toolchain, buildConfig, projectPath,
            installConfigPath, target));
    model.put("stripDebug", lib.isStripDebug());
    return model;
  }

  private Map<String, Object> buildExecutableModel(final CMakeResolvedExecutable exec,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final Map<String, Object> model = new HashMap<>();
    final String target = CMakeFileConventions.buildTarget(exec.getName(), toolchain.getName(), buildConfig);
    model.put("target", target);
    model.put("packageDependencies", exec.getAllPackageDependencies());
    model.put("projectIncludes", buildFilteredProjectIncludes(exec.getAllProjectDependencies(), toolchain, buildConfig));

    final Path projectPath = getProjectDirectory().getAsFile().toPath();
    model.put("headerDirs", buildRelativePaths(exec.getHeaders(), projectPath));
    model.put("sourcePaths", buildRelativeFilePaths(exec.getSources(), projectPath));

    populateCompileModel(model, exec);
    populateLinkModel(model, exec, toolchain, buildConfig, false);

    model.put("outputName", exec.getOutputName());

    final Path configPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH)
        .dir(toolchain.getName()).dir(buildConfig).dir(target).getAsFile().toPath();
    model.put("configRelPath", projectPath.relativize(configPath).toString());
    model.put("buildConfigs", toolchain.getBuildConfigs());

    final Path installConfigPath = configPath.resolve("bin");
    model.put("installDir", projectPath.relativize(installConfigPath).toString());
    model.put("installDependencies",
        buildInstallDependencies(exec.getPrivateProjectDependencies(), toolchain, buildConfig, projectPath,
            installConfigPath, target));
    model.put("stripDebug", exec.isStripDebug());
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

  private List<String> buildFilteredProjectIncludes(final Collection<CMakeResolvedProjectDependency> deps,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    final List<String> includes = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dep : deps) {
      if (!Objects.equals(getProjectName(), dep.getProjectName())) {
        includes.add(CMakeFileConventions.moduleTarget(dep.getProjectName(), dep.getName(), dep.getLinkType(),
            toolchain.getName(), buildConfig));
      }
    }
    return includes;
  }

  private List<String> buildRelativePaths(final Collection<File> dirs, final Path base) {
    final List<String> paths = new ArrayList<>();
    for (final File dir : dirs) {
      paths.add(base.relativize(dir.toPath()).toString());
    }
    return paths;
  }

  private List<String> buildRelativeFilePaths(final Collection<File> files, final Path base) {
    final List<String> paths = new ArrayList<>();
    for (final File file : files) {
      paths.add(base.relativize(file.toPath()).toString());
    }
    return paths;
  }

  private List<String> buildLinkLibraries(final Collection<String> options,
      final Collection<CMakeResolvedProjectDependency> projectDeps,
      final Collection<CMakeResolvedPackageDependency> packageDeps, final CMakeResolvedToolchain toolchain,
      final String buildConfig) {
    final List<String> libs = new ArrayList<>();
    for (final String option : options) {
      libs.add(option);
    }
    for (final CMakeResolvedProjectDependency dep : projectDeps) {
      libs.add(CMakeFileConventions.buildTarget(dep.getProjectName(), dep.getName(), dep.getLinkType(),
          toolchain.getName(), buildConfig));
    }
    for (final CMakeResolvedPackageDependency dep : packageDeps) {
      libs.add(dep.getTargetPrefix() + "::" + dep.getName());
    }
    return libs;
  }

  private List<Map<String, Object>> buildInstallDependencies(final Collection<CMakeResolvedProjectDependency> deps,
      final CMakeResolvedToolchain toolchain, final String buildConfig, final Path projectPath,
      final Path installConfigPath, final String componentTarget) {
    final List<Map<String, Object>> installDeps = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dep : deps) {
      final Map<String, Object> installDep = new HashMap<>();
      installDep.put("componentTarget", componentTarget);
      if (Objects.equals(getProjectName(), dep.getProjectName())
          && Objects.equals(CMakeLinkVariant.SHARED, dep.getLinkType())) {
        installDep.put("type", "local_shared");
        installDep.put("configLibPath", projectPath.relativize(installConfigPath).toString() + "/lib");
        installDep.put("buildTarget",
            CMakeFileConventions.buildTarget(dep.getName(), dep.getLinkType(), toolchain.getName(), buildConfig));
      } else {
        installDep.put("type", "imported");
        installDep.put("buildTarget", CMakeFileConventions.buildTarget(dep.getProjectName(), dep.getName(),
            dep.getLinkType(), toolchain.getName(), buildConfig));
      }
      installDeps.add(installDep);
    }
    return installDeps;
  }

}
