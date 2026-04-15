/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeLibraryCompiling extends CMakeCompiling {

  public CMakeLibraryCompiling() {
    super(CMakeVisibility.PUBLIC);
  }

}
