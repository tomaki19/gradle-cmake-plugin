/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

class CMakeBinaryDependenciesTest {

  @Test
  void testConstructor() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("lib1", "lib2");
    assertEquals(2, deps.getNames().size());
  }

  @Test
  void testFrom() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("mylib");
    deps.setFrom("myproject");
    assertEquals("myproject", deps.getFrom());
  }

  @Test
  void testGetLinkage() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("mylib");
    deps.setLinkType(io.github.tomaki19.gradle.cmake.model.CMakeLinkType.STATIC);
    assertEquals("static", deps.getLinkType().toLowerCase());
  }

  @Test
  void testHashCode() {
    CMakeBinaryDependencies deps1 = new TestCMakeBinaryDependencies("mylib");
    CMakeBinaryDependencies deps2 = new TestCMakeBinaryDependencies("mylib");

    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEquals() {
    CMakeBinaryDependencies deps1 = new TestCMakeBinaryDependencies("mylib");
    CMakeBinaryDependencies deps2 = new TestCMakeBinaryDependencies("mylib");

    assertEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentNames() {
    CMakeBinaryDependencies deps1 = new TestCMakeBinaryDependencies("lib1");
    CMakeBinaryDependencies deps2 = new TestCMakeBinaryDependencies("lib2");

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentFrom() {
    CMakeBinaryDependencies deps1 = new TestCMakeBinaryDependencies("mylib");
    deps1.setFrom("project1");

    CMakeBinaryDependencies deps2 = new TestCMakeBinaryDependencies("mylib");
    deps2.setFrom("project2");

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("mylib");

    assertFalse(deps.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeBinaryDependencies deps = new TestCMakeBinaryDependencies("mylib");

    assertFalse(deps.equals("not a dependency"));
  }

  private static class TestCMakeBinaryDependencies extends CMakeBinaryDependencies {
    TestCMakeBinaryDependencies(CharSequence... names) {
      super(CMakeLinkType.SHARED, CMakeVisibilityType.PUBLIC, names);
    }
  }
}
