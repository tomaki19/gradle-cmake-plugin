/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;

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
    Collection<CMakeBuildItems> options = linking.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testGetDependencies() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    Collection<CMakeLibraryDependencies> dependencies = linking.getDependencies();
    assertNotNull(dependencies);
    assertEquals(0, dependencies.size());
  }

  @Test
  void testOption() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.options("-L/usr/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(new CMakeBuildItems(false, "-L/usr/lib"), linking.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    linking.options("-L/usr/lib", "-L/usr/local/lib");
    assertEquals(1, linking.getOptions().size());
    assertEquals(new CMakeBuildItems(false, "-L/usr/lib", "-L/usr/local/lib"), linking.getOptions().iterator().next());
  }

  @Test
  void testDependencyCharSequence() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    CMakeLibraryDependencies dep = linking.dependencies("mylib");
    assertNotNull(dep);
    assertEquals(1, linking.getDependencies().size());
  }

  @Test
  void testDependenciesVarargs() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    CMakeLibraryDependencies dep = linking.dependencies("lib1", "lib2");
    assertNotNull(dep);
    assertEquals(1, linking.getDependencies().size());
  }

  @Test
  void testDependenciesCollection() {
    CMakeLibraryLinking linking = new CMakeLibraryLinking();
    CMakeLibraryDependencies dep1 = new CMakeLibraryDependencies("lib1");
    CMakeLibraryDependencies dep2 = new CMakeLibraryDependencies("lib2");
    linking.dependencies(java.util.Arrays.asList(dep1, dep2));
    assertEquals(2, linking.getDependencies().size());
  }
}
