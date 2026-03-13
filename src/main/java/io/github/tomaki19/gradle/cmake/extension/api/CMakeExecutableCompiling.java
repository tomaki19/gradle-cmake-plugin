/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

public class CMakeExecutableCompiling extends CMakeCompiling {

  public CMakeExecutableCompiling() {
    super(true);
  }

}
