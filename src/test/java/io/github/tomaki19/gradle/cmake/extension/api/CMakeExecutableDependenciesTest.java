/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;

class CMakeExecutableDependenciesTest {

  @Test
  void testConstructor() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(Map.of(), "mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(Map.of(), "lib1", "lib2");
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.PROJECT, "myProject"), "mylib");
    assertEquals("myProject", deps.getProject());
  }

  @Test
  void testGetLinkStatic() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, CMakeLinkVariant.STATIC), "mylib");
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, CMakeLinkVariant.SHARED), "mylib");
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, CMakeLinkVariant.INTERFACE), "mylib");
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }
}
