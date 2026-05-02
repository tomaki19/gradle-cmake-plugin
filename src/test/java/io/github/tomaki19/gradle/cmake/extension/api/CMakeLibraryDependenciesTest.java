/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeLibraryDependenciesTest {

  @Test
  void testConstructor() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(List.of("lib1", "lib2"), Map.of());
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.PROJECT, "myProject"));
    assertEquals("myProject", deps.getProject());
  }

  @Test
  void testGetLinkStatic() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "static"));
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "shared"));
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "interface"));
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "static"));
    assertEquals("static", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared"));
    assertEquals("shared", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testEqualsAndHashCode() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"),
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared", CMakeLibraryLinkSpec.LINK_VARIANT, "static",
            CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"));
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"),
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared", CMakeLibraryLinkSpec.LINK_VARIANT, "static",
            CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"));
    assertEquals(deps1, deps2);
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEqualsAndHashCodeWithDifferentBuildVariant() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared"));
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        List.of("mylib"), Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "static"));
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertNotEquals(null, deps);
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertNotEquals(new Object(), deps);
  }
}
