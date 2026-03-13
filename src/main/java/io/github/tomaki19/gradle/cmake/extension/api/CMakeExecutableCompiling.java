/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeExecutableCompiling extends CMakeCompiling {

  public CMakeExecutableCompiling() {
    super(CMakeVisibilityType.PRIVATE);
  }

}
