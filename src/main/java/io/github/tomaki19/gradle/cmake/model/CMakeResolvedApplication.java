/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;

public final class CMakeResolvedApplication extends CMakeResolvedBinary<CMakeResolvedApplication> {

  public CMakeResolvedApplication(final CMakeApplication application, final boolean stripDebug) {
    super(application, stripDebug);
  }

}
