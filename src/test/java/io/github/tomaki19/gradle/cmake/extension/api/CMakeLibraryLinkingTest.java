/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeLibraryLinkingTest {

  @Test
  void testConstructor() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    assertNotNull(linking);
  }

  @Test
  void testGetOptions() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    Collection<CMakeBuildSpec> options = linking.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testGetDependencies() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    Collection<CMakeLibraryLinkSpec> dependencies = linking.getDependencySpecs();
    assertNotNull(dependencies);
    assertEquals(0, dependencies.size());
  }

  @Test
  void testOption() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.options(Map.of(), "-L/usr/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-L/usr/lib"),
        linking.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.options(Map.of(), "-L/usr/lib", "-L/usr/local/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-L/usr/lib", "-L/usr/local/lib"),
        linking.getOptions().iterator().next());
  }

  @Test
  void testDependency() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.link(Map.of(), "mylib");
    assertEquals(1, linking.getDependencySpecs().size());
  }

  @Test
  void testDependenciesMultipleComponents() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.link(Map.of(), "lib1", "lib2");
    assertEquals(1, linking.getDependencySpecs().size());
  }
}
