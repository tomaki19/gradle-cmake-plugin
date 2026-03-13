/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

class CMakeExecutableLinkingTest {

  @Test
  void testConstructor() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    assertNotNull(linking);
  }

  @Test
  void testGetOptions() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    Collection<CMakeBuildItems> options = linking.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testGetDependencies() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    Collection<CMakeExecutableDependencies> dependencies = linking.getDependencies();
    assertNotNull(dependencies);
    assertEquals(0, dependencies.size());
  }

  @Test
  void testOption() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.options("-L/usr/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(new CMakeBuildItems(CMakeVisibilityType.PRIVATE,"-L/usr/lib"), linking.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    linking.options("-L/usr/lib", "-L/usr/local/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(new CMakeBuildItems(CMakeVisibilityType.PRIVATE,"-L/usr/lib", "-L/usr/local/lib"), linking.getOptions().iterator().next());
  }

  @Test
  void testDependencyCharSequence() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    CMakeBinaryDependencies dep = linking.dependency("mylib");
    assertNotNull(dep);
    assertEquals(1, linking.getDependencies().size());
  }

  @Test
  void testDependencyObject() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    CMakeExecutableDependencies dep = new CMakeExecutableDependencies("mylib");
    linking.dependency(dep);
    assertEquals(1, linking.getDependencies().size());
  }

  @Test
  void testDependenciesVarargs() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    CMakeExecutableDependencies dep = linking.dependencies("lib1", "lib2");
    assertNotNull(dep);
    assertEquals(1, linking.getDependencies().size());
  }

  @Test
  void testDependenciesCollection() {
    CMakeExecutableLinking linking = new CMakeExecutableLinking();
    CMakeExecutableDependencies dep1 = new CMakeExecutableDependencies("lib1");
    CMakeExecutableDependencies dep2 = new CMakeExecutableDependencies("lib2");
    linking.dependencies(java.util.Arrays.asList(dep1, dep2));
    assertEquals(2, linking.getDependencies().size());
  }
}
