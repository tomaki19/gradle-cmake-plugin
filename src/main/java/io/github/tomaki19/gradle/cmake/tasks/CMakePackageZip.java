/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.file.Directory;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Zip;

import io.github.tomaki19.gradle.cmake.files.CMakeFileConventions;

@CacheableTask
public abstract class CMakePackageZip extends Zip {

  @javax.inject.Inject
  public CMakePackageZip(final Directory directory) {
    setGroup(CMakeTaskRegistry.GROUP_PACKAGE);
    getArchiveVersion().set(getProject().getVersion().toString());
    from(directory);
    getDestinationDirectory().dir(getProject().getLayout().getBuildDirectory().get()
        .dir(CMakeFileConventions.CMAKE_INSTALL_PATH).getAsFile().getAbsolutePath());
  }
}
