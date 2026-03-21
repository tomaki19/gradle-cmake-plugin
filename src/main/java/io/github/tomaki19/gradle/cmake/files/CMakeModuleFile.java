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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.file.Directory;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
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
    processTemplate("module-file.ftl", buildModel(), outputStream);
  }

  private Map<String, Object> buildModel() {
    final Map<String, Object> model = new HashMap<>();

    final String target = CMakeFileConventions.buildTarget(getProjectName(), library.getName(),
        library.getLinkType(), toolchain.getName(), buildConfig);
    model.put("target", target);
    model.put("linkType", library.getLinkType().name());
    model.put("buildConfigUpper", buildConfig.toUpperCase());
    model.put("packageDependencies", library.getAllPackageDependencies());

    final List<String> projectIncludes = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dep : library.getAllProjectDependencies()) {
      projectIncludes.add(CMakeFileConventions.moduleTarget(dep.getProjectName(), dep.getName(),
          dep.getLinkType(), toolchain.getName(), buildConfig));
    }
    model.put("projectIncludes", projectIncludes);

    final Path targetPath = Paths.get(toolchain.getName(), buildConfig,
        CMakeFileConventions.buildTarget(library.getName(), library.getLinkType(), toolchain.getName(), buildConfig));
    model.put("targetPath", targetPath.toString());

    final OperatingSystem os = OperatingSystem.current();
    model.put("isLinux", os.isLinux());
    model.put("isWindows", os.isWindows());

    if (library.getLinkType() == CMakeLinkVariant.SHARED) {
      model.put("sharedLibName", os.getSharedLibraryName(library.getOutputName()));
      if (os.isLinux()) {
        model.put("soname", os.getLinkLibraryName(library.getOutputName()));
      }
      if (os.isWindows()) {
        model.put("implibName", os.getLinkLibraryName(library.getOutputName()));
      }
    } else if (library.getLinkType() == CMakeLinkVariant.STATIC) {
      model.put("staticLibName", os.getStaticLibraryName(library.getOutputName()));
    }

    final Path exportPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH).getAsFile().toPath();
    final List<String> headerRelPaths = new ArrayList<>();
    for (final File headerDir : library.getHeaders()) {
      headerRelPaths.add(exportPath.relativize(headerDir.toPath()).toString());
    }
    model.put("headerRelPaths", headerRelPaths);

    model.put("publicCompileOptions", library.getPublicCompileOptions());
    model.put("privateCompileDefinitions", library.getPrivateCompileDefinitions());

    final List<String> publicProjectDepTargets = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dep : library.getPublicProjectDependencies()) {
      publicProjectDepTargets.add(CMakeFileConventions.buildTarget(dep.getProjectName(), dep.getName(),
          dep.getLinkType(), toolchain.getName(), buildConfig));
    }
    model.put("publicProjectDepTargets", publicProjectDepTargets);

    final List<String> publicPackageLinkLibraries = new ArrayList<>();
    for (final CMakeResolvedPackageDependency dep : library.getPublicPackageDependencies()) {
      publicPackageLinkLibraries.add(dep.getTargetPrefix() + "::" + dep.getName());
    }
    model.put("publicPackageLinkLibraries", publicPackageLinkLibraries);

    model.put("publicLinkOptions", library.getPublicLinkOptions());

    return model;
  }

}
