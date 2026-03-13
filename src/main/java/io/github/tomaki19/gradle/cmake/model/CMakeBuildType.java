/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public enum CMakeBuildType {
  STATIC, SHARED, MODULE;

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
