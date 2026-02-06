/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeDependencies;

public class TestCMakeDependencies {

  public static CMakeDependencies create(String... names) {
    return new CMakeDependencies(names);
  }

  private TestCMakeDependencies() {
  }
}
