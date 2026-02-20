/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CMakeLibraryDependenciesTest {

  @Test
  void testConstructor() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("lib1", "lib2");
    assertEquals(2, deps.getNames().size());
  }

  @Test
  void testFrom() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.from("myproject");
    assertEquals("myproject", deps.getFrom().get());
  }

  @Test
  void testGetLinkStatic() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.getLinkStatic();
    assertEquals("static", deps.getLinkage().get().toString());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.getLinkShared();
    assertEquals("shared", deps.getLinkage().get().toString());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.getLinkInterface();
    assertEquals("interface", deps.getLinkage().get().toString());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.getForStaticBuild();
    assertEquals("static", deps.getBuildType().get().toString());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.getForSharedBuild();
    assertEquals("shared", deps.getBuildType().get().toString());
  }
}
