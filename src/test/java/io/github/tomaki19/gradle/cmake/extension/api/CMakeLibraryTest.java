/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;

class CMakeLibraryTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());
    assertNotNull(library);
  }

  @Test
  void testCompile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    library.compiling(compile -> {
      compile.defines(Map.of(), "TEST_DEFINE");
    });
  }

  @Test
  void testLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    library.linking(linking -> {
      linking.options(Map.of(), "-Wl,--no-undefined");
    });
  }

}
