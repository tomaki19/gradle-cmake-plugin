/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

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
    deps.from("myproject");
    assertEquals("myproject", deps.getFrom().get());
  }

  @Test
  void testGetLinkStatic() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.linkStatic();
    assertEquals("static", deps.getLinkType().get().toString());
  }

  @Test
  void testGetLinkShared() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.linkShared();
    assertEquals("shared", deps.getLinkType().get().toString());
  }

  @Test
  void testGetLinkInterface() {
    CMakeExecutableDependencies deps = new CMakeExecutableDependencies("mylib");
    deps.linkInterface();
    assertEquals("interface", deps.getLinkType().get().toString());
  }
}
