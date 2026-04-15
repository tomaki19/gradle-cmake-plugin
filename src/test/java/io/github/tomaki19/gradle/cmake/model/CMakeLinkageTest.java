/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CMakeLinkageTest {

  @Test
  void testLinkageValues() {
    assertNotNull(CMakeLinkVariant.STATIC);
    assertNotNull(CMakeLinkVariant.SHARED);
    assertNotNull(CMakeLinkVariant.INTERFACE);
  }

  @Test
  void testLinkageToString() {
    assertEquals("static", CMakeLinkVariant.STATIC.toLowerCase());
    assertEquals("shared", CMakeLinkVariant.SHARED.toLowerCase());
    assertEquals("interface", CMakeLinkVariant.INTERFACE.toLowerCase());
  }

  @Test
  void testVisibilityTypeValues() {
    assertNotNull(CMakeVisibility.PUBLIC);
    assertNotNull(CMakeVisibility.PRIVATE);
  }

  @Test
  void testVisibilityTypeToLowerCase() {
    assertEquals("public", CMakeVisibility.PUBLIC.toLowerCase());
    assertEquals("private", CMakeVisibility.PRIVATE.toLowerCase());
  }

  @Test
  void testBuildVariantToLowerCase() {
    assertEquals("static", CMakeBuildVariant.STATIC.toLowerCase());
    assertEquals("shared", CMakeBuildVariant.SHARED.toLowerCase());
    assertEquals("module", CMakeBuildVariant.MODULE.toLowerCase());
  }
}
