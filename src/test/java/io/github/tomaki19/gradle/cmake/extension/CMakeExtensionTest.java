/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;

class CMakeExtensionTest {

  @Test
  void testExtensionValues() {
    assertEquals("cmake", MockCMakeExtension.NAME);
  }

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    assertNotNull(extension);
  }

  @Test
  void testMockCMakeApplication() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeApplication app = new MockCMakeApplication("mockApp", project.getObjects());
    assertNotNull(app);
    assertEquals("mockApp", app.getName());
  }

  @Test
  void testRegisterMethods() {
    final Project project = ProjectBuilder.builder().build();
    final MockCMakeExtension extension = new MockCMakeExtension(project.getObjects());
    // No exception should be thrown for each overload
    extension.register("myTask", t -> {
    });
    extension.register("myTask", java.util.Collections.singletonList("myToolchain"), t -> {
    });
    extension.register("myTask", java.util.Collections.singletonList("myToolchain"),
        java.util.Collections.singletonList("debug"), t -> {
        });
  }

}
