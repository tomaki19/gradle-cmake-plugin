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

class CMakeExecutableLinkingTest {

  @Test
  void testConstructor() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    assertNotNull(linking);
  }

  @Test
  void testGetOptions() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    Collection<CMakeBuildSpec> options = linking.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testGetDependencies() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    Collection<CMakeExecutableLinkSpec> dependencies = linking.getDependencySpecs();
    assertNotNull(dependencies);
    assertEquals(0, dependencies.size());
  }

  @Test
  void testOption() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.options(Map.of(), "-L/usr/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-L/usr/lib"),
        linking.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.options(Map.of(), "-L/usr/lib", "-L/usr/local/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-L/usr/lib", "-L/usr/local/lib"),
        linking.getOptions().iterator().next());
  }

  @Test
  void testDependency() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.link(Map.of(), "mylib");
    assertEquals(1, linking.getDependencySpecs().size());
  }

  @Test
  void testDependenciesMultipleComponents() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.link(Map.of(), "lib1", "lib2");
    assertEquals(1, linking.getDependencySpecs().size());
    CMakeExecutableLinkSpec dep = linking.getDependencySpecs().iterator().next();
    assertEquals(2, dep.getComponents().size());
  }
}
