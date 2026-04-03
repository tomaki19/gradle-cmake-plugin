/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class CMakeResolvedExecutable extends CMakeResolvedBinary<CMakeResolvedExecutable> {

  public CMakeResolvedExecutable(final CMakeApplication application, final boolean stripDebug) {
    super(application, stripDebug);
  }

  public CMakeResolvedExecutable(final CMakeTest test, final boolean stripDebug) {
    super(test, stripDebug);
  }

}
