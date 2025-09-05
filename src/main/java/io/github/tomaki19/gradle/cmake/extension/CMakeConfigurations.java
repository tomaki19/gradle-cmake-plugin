/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

public enum CMakeConfigurations {

  CMAKE_PROJECT("cmakeProject"),
  CMAKE_RUNTIME("cmakeRuntime");

  private final String name;

  CMakeConfigurations(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
