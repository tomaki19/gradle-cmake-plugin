/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeExtension;

class CMakeExtensionTest {

  @Test
  void testExtensionValues() {
    assertEquals("cmake", CMakeExtension.NAME);
  }

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = new MockCMakeExtension(project.getObjects());
    assertNotNull(extension);
  }

}
