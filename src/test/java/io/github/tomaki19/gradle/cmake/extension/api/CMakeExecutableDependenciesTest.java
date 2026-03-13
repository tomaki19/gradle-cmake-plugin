/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

class CMakeExecutableDependenciesTest {

  @Test
  void testConstructor() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("lib1", "lib2");
    assertEquals(2, deps.getNames().size());
  }

  @Test
  void testFrom() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.from("myProject");
    assertEquals("myProject", deps.getFrom());
  }

  @Test
  void testGetLinkStatic() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.link(CMakeLinkType.STATIC);
    assertEquals("static", deps.getLinkType().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.link(CMakeLinkType.SHARED);
    assertEquals("shared", deps.getLinkType().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.link(CMakeLinkType.INTERFACE);
    assertEquals("interface", deps.getLinkType().toLowerCase());
  }
}
