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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.Project;

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
      final String buildConfig, final Project project) {
    super("%s.cmake".formatted(CMakeFileConventions.moduleTarget(project, library, toolchain, buildConfig)), project);
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

    final String target = CMakeFileConventions.buildTarget(library, toolchain, buildConfig);
    model.put("target", "%s::%s".formatted(getProjectName(), target));
    model.put("linkType", library.getLinkVariant().name());
    model.put("buildConfigUpper", buildConfig.toUpperCase());
    model.put("packageDependencies", library.getAllPackageDependencies());

    final List<String> projectIncludes = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dependency : library.getAllProjectDependencies()) {
      projectIncludes.add(CMakeFileConventions.moduleTarget(dependency, toolchain, buildConfig));
    }
    model.put("projectIncludes", projectIncludes);

    final Path exportPath = getBuildDirectory().dir(CMakeFileConventions.CMAKE_CONFIG_PATH).getAsFile().toPath();
    final Path targetPath = CMakeFileConventions
        .targetBinaryDirectory(getBuildDirectory(), library, toolchain, buildConfig).getAsFile().toPath();
    model.put("targetRelPath", exportPath.relativize(targetPath));

    model.put("isLinux", toolchain.getOperatingSystem().isLinux());
    model.put("isWindows", toolchain.getOperatingSystem().isWindows());

    if (library.getLinkVariant() == CMakeLinkVariant.SHARED) {
      model.put("sharedLibName", toolchain.getOperatingSystem().getSharedLibraryName(library.getOutputName()));
      if (toolchain.getOperatingSystem().isLinux()) {
        model.put("soname", toolchain.getOperatingSystem().getLinkLibraryName(library.getOutputName()));
      }
      if (toolchain.getOperatingSystem().isWindows()) {
        model.put("implibName", toolchain.getOperatingSystem().getLinkLibraryName(library.getOutputName()));
      }
    } else if (library.getLinkVariant() == CMakeLinkVariant.STATIC) {
      model.put("staticLibName", toolchain.getOperatingSystem().getStaticLibraryName(library.getOutputName()));
    }

    final List<String> headerRelPaths = new ArrayList<>();
    for (final File headerDir : library.getHeaders()) {
      headerRelPaths.add(exportPath.relativize(headerDir.toPath()).toString());
    }
    model.put("headerRelPaths", headerRelPaths);

    model.put("publicCompileOptions", library.getPublicCompileOptions());
    model.put("publicCompileDefinitions", library.getPublicCompileDefinitions());

    final List<String> publicProjectDepTargets = new ArrayList<>();
    for (final CMakeResolvedProjectDependency dependency : library.getPublicProjectDependencies()) {
      publicProjectDepTargets.add("%s::%s".formatted(dependency.getProjectName(),
          CMakeFileConventions.buildTarget(dependency, toolchain, buildConfig)));
    }
    model.put("publicProjectDepTargets", publicProjectDepTargets);

    final List<String> publicPackageLinkLibraries = new ArrayList<>();
    for (final CMakeResolvedPackageDependency dependency : library.getPublicPackageDependencies()) {
      publicPackageLinkLibraries.add("%s::%s".formatted(dependency.getTargetPrefix(), dependency.getName()));
    }
    model.put("publicPackageLinkLibraries", publicPackageLinkLibraries);

    model.put("publicLinkOptions", library.getPublicLinkOptions());

    return model;
  }

}
