/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeLibraryDependenciesTest {

  @Test
  void testConstructor() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "lib1", "lib2");
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.PROJECT, "myProject"), "mylib");
    assertEquals("myProject", deps.getProject());
  }

  @Test
  void testGetLinkStatic() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "static"), "mylib");
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "shared"), "mylib");
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, "interface"), "mylib");
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "static"), "mylib");
    assertEquals("static", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared"), "mylib");
    assertEquals("shared", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testEqualsAndHashCode() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared", CMakeLibraryLinkSpec.LINK_VARIANT, "static",
            CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"), "mylib");
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared", CMakeLibraryLinkSpec.LINK_VARIANT, "static",
            CMakeLibraryLinkSpec.VISIBILITY, "PUBLIC"), "mylib");
    assertEquals(deps1, deps2);
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEqualsAndHashCodeWithDifferentBuildVariant() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "shared"), "mylib");
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, "static"), "mylib");
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "mylib");
    assertNotEquals(null, deps);
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "mylib");
    assertNotEquals(new Object(), deps);
  }
}
