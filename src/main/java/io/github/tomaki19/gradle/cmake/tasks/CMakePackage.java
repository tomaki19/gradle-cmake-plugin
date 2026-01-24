/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.bundling.Zip;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakePackage extends Zip {

  @javax.inject.Inject
  CMakePackage(final String buildTarget, final CMakeResolvedToolchain toolchain, final String buildConfig) {
    setGroup(CMakeTaskRegistry.GROUP_PACKAGE);
    getArchiveBaseName().set(buildTarget);
    getDestinationDirectory().set(getProject().getLayout().getBuildDirectory().dir("install").get());
    final String toolchainPath = "%s/%s/%s".formatted(CMakeFileConventions.CMAKE_EXPORT_PATH, toolchain.getName(),
        buildConfig);
    from(getProject().getLayout().getBuildDirectory()
        .dir(toolchainPath).get().getAsFile().toURI().getPath()).include("%s.*".formatted(buildTarget));
  }

}
