/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;
import javax.inject.Inject;
import org.gradle.api.tasks.bundling.Zip;

public abstract class CMakePackage extends Zip {

  @Inject
  public CMakePackage(final String buildTarget, final CMakeResolvedToolchain toolchain) {
    setGroup(CMakeTasksConventions.GROUP_PACKAGE);
    getArchiveBaseName().set(buildTarget);
    getDestinationDirectory().set(getProject().getLayout().getBuildDirectory().dir("install").get());
    final String toolchainPath = "%s/%s".formatted(CMakeListsConventions.CMAKE_INSTALL_PATH, toolchain.getName());
    from(getProject().getLayout().getBuildDirectory()
        .dir(toolchainPath).get().getAsFile().toURI().getPath()).include("%s.*".formatted(buildTarget));
  }
}
