/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Zip;

@CacheableTask
public abstract class CMakePackageDevelopment extends Zip {

  @javax.inject.Inject
  public CMakePackageDevelopment() {
    setGroup(CMakeTaskRegistry.GROUP_PACKAGE);
    getArchiveBaseName().set(getProject().getName());
  }

}
