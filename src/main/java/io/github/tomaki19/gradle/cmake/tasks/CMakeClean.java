/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Delete;

import io.github.tomaki19.gradle.cmake.files.CMakeListsFile;

@CacheableTask
public abstract class CMakeClean extends Delete {

  @javax.inject.Inject
  public CMakeClean() {
    delete(getProject().getLayout().getProjectDirectory().file(CMakeListsFile.NAME));
  }

}
