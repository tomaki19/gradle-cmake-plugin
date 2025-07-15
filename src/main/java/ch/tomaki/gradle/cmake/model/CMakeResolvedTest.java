/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.api.CMakeTest;

public final class CMakeResolvedTest extends CMakeResolvedBinary<CMakeResolvedTest> {

  CMakeResolvedTest(final CMakeTest test, final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) {
    super(test, buildStatic, buildShared, stripDebug, packageBuildOutputs);
  }

}
