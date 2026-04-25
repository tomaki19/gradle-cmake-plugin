/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class CMakeResolvedTest extends CMakeResolvedBinary<CMakeResolvedTest> {

  public CMakeResolvedTest(final CMakeTest test, final boolean stripDebug) {
    super(test, stripDebug);
  }

}
