/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.Delete;
import org.gradle.work.DisableCachingByDefault;

import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;

@DisableCachingByDefault(because = "Deletion cannot be cached")
public abstract class CMakeClean extends Delete {

  @javax.inject.Inject
  public CMakeClean() {
    delete(getProject().getLayout().getProjectDirectory().file(CMakeListsFile.NAME));
  }

}
