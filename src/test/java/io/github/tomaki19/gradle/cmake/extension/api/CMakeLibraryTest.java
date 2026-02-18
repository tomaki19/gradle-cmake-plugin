/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
  void testPublicCompile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    // Test public compile action
    library.publicCompile(compile -> {
      compile.define("TEST_DEFINE");
    });
  }

  @Test
  void testPublicLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    // Test public linking action
    library.publicLinking(linking -> {
      linking.option("-Wl,--no-undefined");
    });
  }

  @Test
  void testPublicInterfaceLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    // Test public interface linking action
    library.publicLinking(linking -> {
      linking.option("-Wl,--no-undefined");
    });
  }

  @Test
  void testPublicStaticLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    // Test public static linking action
    library.publicLinking(linking -> {
      linking.option("-static");
    });
  }

  @Test
  void testPublicSharedLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("test", project.getObjects());

    // Test public shared linking action
    library.publicLinking(linking -> {
      linking.option("-shared");
    });
  }
}
