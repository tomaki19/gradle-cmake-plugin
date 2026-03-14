/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;

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
    deps.link(CMakeLinkVariant.STATIC);
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.link(CMakeLinkVariant.SHARED);
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.link(CMakeLinkVariant.INTERFACE);
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.forBuildVariant(CMakeBuildVariant.STATIC);
    assertEquals("static", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.forBuildVariant(CMakeBuildVariant.SHARED);
    assertEquals("shared", deps.getBuildVariant().toLowerCase());
  }
}
