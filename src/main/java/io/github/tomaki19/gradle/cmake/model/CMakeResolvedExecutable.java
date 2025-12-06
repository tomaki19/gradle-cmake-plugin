/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class CMakeResolvedExecutable extends CMakeResolvedBinary<CMakeResolvedExecutable> {

  CMakeResolvedExecutable(final CMakeApplication test, final boolean stripDebug, final boolean packageBuildOutputs) {
    super(test, stripDebug, packageBuildOutputs);
  }

  CMakeResolvedExecutable(final CMakeTest test, final boolean stripDebug, final boolean packageBuildOutputs) {
    super(test, stripDebug, packageBuildOutputs);
  }

}
