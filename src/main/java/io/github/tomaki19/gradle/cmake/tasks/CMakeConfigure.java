/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.file.Directory;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeConfigure extends CMakeExec {

  @javax.inject.Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s/%s".formatted(CMakeFileConventions.CMAKE_CONFIG_PATH, toolchain.getName(), buildConfig)).get();
    // tasks with same output directory are not run in parallel
    setWorkingDir(getProject().getProjectDir());
    // if gradle build file changes, configure needs to be run
    getInputs().file(getProject().getBuildFile());
    getBaseCommand().set(OperatingSystem.current().getExecutableName("cmake"));
    getBaseArguments().add("-S \"%s\"".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    getBaseArguments().add("-B \"%s\"".formatted(outputDirectory.getAsFile().getAbsolutePath()));
    getBaseArguments().add("-G \"%s\"".formatted(toolchain.getGenerator()));
    getBaseArguments().add("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchain.getName()));
    getBaseArguments().add("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(buildConfig));
    final Set<String> exportPaths = new HashSet<>();
    getProject().getRootProject().getAllprojects().forEach((subproject) -> {
      if (!Objects.equals(subproject.getName(), getProject().getName())
          && !Objects.equals(subproject.getName(), getProject().getRootProject().getName())) {
        exportPaths.add(subproject.getLayout().getBuildDirectory().get()
            .dir(CMakeFileConventions.CMAKE_EXPORT_PATH).getAsFile().toPath().toString());
      }
    });
    getBaseArguments().add("-DCMAKE_MODULE_PATH=\"%s\"".formatted(String.join(";", exportPaths)));
    toolchain.getToolchainFile().ifPresent((toolchainFile) -> {
      getBaseArguments().add("--toolchain \"%s\"".formatted(toolchainFile.getAsFile().getAbsolutePath()));
    });
  }
}
