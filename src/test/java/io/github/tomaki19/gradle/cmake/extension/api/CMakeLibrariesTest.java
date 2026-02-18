/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibraries;

class CMakeLibrariesTest {

  @Test
  void testConstructor() {
    final CMakeLibraries libraries = new MockCMakeLibraries();
    assertNotNull(libraries);
  }
}
