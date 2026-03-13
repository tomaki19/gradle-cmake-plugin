/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildType;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

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
    deps.from("myProject");
    assertEquals("myProject", deps.getFrom());
  }

  @Test
  void testGetLinkStatic() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.link(CMakeLinkType.STATIC);
    assertEquals("static", deps.getLinkType().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.link(CMakeLinkType.SHARED);
    assertEquals("shared", deps.getLinkType().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.link(CMakeLinkType.INTERFACE);
    assertEquals("interface", deps.getLinkType().toLowerCase());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.build(CMakeBuildType.STATIC);
    assertEquals("static", deps.getBuildType().toLowerCase());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.build(CMakeBuildType.SHARED);
    assertEquals("shared", deps.getBuildType().toLowerCase());
  }
}
