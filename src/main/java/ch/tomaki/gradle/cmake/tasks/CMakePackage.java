/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import org.gradle.api.tasks.bundling.Zip;

import ch.tomaki.gradle.cmake.files.CMakeFileConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

abstract class CMakePackage extends Zip {

  @javax.inject.Inject
  CMakePackage(final String buildTarget, final CMakeResolvedToolchain toolchain) {
    setGroup(CMakeTaskRegistry.GROUP_PACKAGE);
    getArchiveBaseName().set(buildTarget);
    getDestinationDirectory().set(getProject().getLayout().getBuildDirectory().dir("install").get());
    final String toolchainPath = "%s/%s".formatted(CMakeFileConventions.CMAKE_INSTALL_PATH, toolchain.getName());
    from(getProject().getLayout().getBuildDirectory()
        .dir(toolchainPath).get().getAsFile().toURI().getPath()).include("%s.*".formatted(buildTarget));
  }

}
