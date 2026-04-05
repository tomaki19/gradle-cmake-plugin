/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

@CacheableTask
public abstract class CMakeConfigure extends CMakeExec {

  private final Set<File> dependencies = new HashSet<>();

  @javax.inject.Inject
  public CMakeConfigure(final CMakeResolvedToolchain toolchain, final String buildConfig) {
    super(toolchain.getName(), buildConfig, toolchain.getEnvironmentFile());
    // tasks with same output directory are not run in parallel
    setWorkingDir(getProject().getProjectDir());
    // if gradle build file changes, configure needs to be run
    getInputs().file(getProject().getBuildFile());
    setExecutable(OperatingSystem.current().getExecutableName("cmake"));
    args("-S \"%s\"".formatted(getProject().getLayout().getProjectDirectory()
        .getAsFile().getAbsolutePath()));
    args("-B \"%s\"".formatted(CMakeFileConventions
        .targetConfigDirectory(getProject().getLayout().getBuildDirectory().get(), toolchain, buildConfig)
        .getAsFile().getAbsolutePath()));
    args("-G \"%s\"".formatted(toolchain.getGenerator()));
    args("-DCMAKE_TOOLCHAIN_NAME=\"%s\"".formatted(toolchain.getName()));
    args("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(buildConfig));
    toolchain.getToolchainFile().ifPresent((toolchainFile) -> {
      args("--toolchain \"%s\"".formatted(toolchainFile.getAsFile().getAbsolutePath()));
    });
  }

  public void addModuleDependencies(final Set<File> moduleDependencies) {
    dependencies.addAll(moduleDependencies);
  }

  @Override
  protected void exec() {
    args("-DCMAKE_MODULE_PATH=\"%s\"".formatted(dependencies.stream().map((file) -> file.getAbsolutePath())
        .collect(Collectors.joining(";"))));
    super.exec();
  }
}
