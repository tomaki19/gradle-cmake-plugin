/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Tar;

import io.github.tomaki19.gradle.cmake.extension.CMakeTaskContainer;

@CacheableTask
public abstract class CMakeCustomTar extends Tar {

  @javax.inject.Inject
  public CMakeCustomTar() {
    setGroup(CMakeTaskContainer.GROUP_DEPLOY);
    getArchiveBaseName().set(getProject().getName());
    getArchiveVersion().set(getProject().getVersion().toString());
  }

}
