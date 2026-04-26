/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Zip;

import io.github.tomaki19.gradle.cmake.extension.CMakeTaskContainer;

@CacheableTask
public abstract class CMakeCustomZip extends Zip {

  @javax.inject.Inject
  public CMakeCustomZip() {
    setGroup(CMakeTaskContainer.GROUP_PACKAGE);
    getArchiveBaseName().set(getProject().getName());
  }

}
