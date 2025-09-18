/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

public enum CMakeConfigurations {

  CMAKE_COMPILE("cmakeCompile"),
  CMAKE_COMPILE_CLASSPATH("cmakeCompileClasspath"),
  CMAKE_COMPILE_ELEMENTS("cmakeCompileElements"),
  CMAKE_RUNTIME("cmakeRuntime"),
  CMAKE_RUNTIME_CLASSPATH("cmakeRuntimeClasspath"),
  CMAKE_RUNTIME_ELEMENTS("cmakeRuntimeElements");

  private final String name;

  CMakeConfigurations(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

}
