/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public enum CMakeLinkVariant {
  STATIC, SHARED, INTERFACE;

  public String toLowerCase() {
    return name().toLowerCase();
  }

}
