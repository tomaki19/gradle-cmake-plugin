/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeTest;

class CMakeTestTest {

  @Test
  void testTestProperties() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test", project.getObjects());
    assertNotNull(test);
  }

  @Test
  void testGetOutputVersion() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test", project.getObjects());
    assertNotNull(test.getOutputVersion());
  }

  @Test
  void testSetOutputVersion() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test", project.getObjects());
    test.getOutputVersion().set("1.0.0");
    assertEquals("1.0.0", test.getOutputVersion().get());
  }
}
