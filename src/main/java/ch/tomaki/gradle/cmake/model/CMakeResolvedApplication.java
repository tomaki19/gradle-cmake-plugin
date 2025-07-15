/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;

public final class CMakeResolvedApplication extends CMakeResolvedBinary<CMakeResolvedApplication> {

  CMakeResolvedApplication(final CMakeApplication application,  final boolean buildStatic, final boolean buildShared,
      final boolean stripDebug, final boolean packageBuildOutputs) {
    super(application,  buildStatic, buildShared, stripDebug, packageBuildOutputs);
  }

}
