/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeExecutableDependenciesTest {

  @Test
  void testConstructor() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(List.of("lib1", "lib2"), Map.of());
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(List.of("mylib"),
        Map.of(CMakeBinaryLinkSpec.PROJECT, "myProject"));
    assertEquals("myProject", deps.getProject());
  }

  @Test
  void testGetLinkStatic() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "static"));
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "shared"));
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeExecutableLinkSpec deps = CMakeExecutableLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "interface"));
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }
}
