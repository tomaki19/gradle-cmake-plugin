/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public enum CMakeVisibility {
  PUBLIC, PRIVATE;

  public String toLowerCase() {
    return name().toLowerCase();
  }
}
