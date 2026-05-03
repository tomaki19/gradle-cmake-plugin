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

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

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
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, CMakeLinkVariant.STATIC), "mylib");
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, CMakeLinkVariant.SHARED), "mylib");
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.LINK_VARIANT, CMakeLinkVariant.INTERFACE), "mylib");
    assertEquals("interface", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetForStaticBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.STATIC), "mylib");
    assertEquals("static", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testGetForSharedBuild() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.SHARED), "mylib");
    assertEquals("shared", deps.getBuildVariant().toLowerCase());
  }

  @Test
  void testEqualsAndHashCode() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.SHARED, CMakeLibraryLinkSpec.LINK_VARIANT, CMakeLinkVariant.STATIC,
            CMakeLibraryLinkSpec.VISIBILITY, CMakeVisibility.PUBLIC), "mylib");
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.SHARED, CMakeLibraryLinkSpec.LINK_VARIANT, CMakeLinkVariant.STATIC,
            CMakeLibraryLinkSpec.VISIBILITY, CMakeVisibility.PUBLIC), "mylib");
    assertEquals(deps1, deps2);
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEqualsAndHashCodeWithDifferentBuildVariant() {
    CMakeLibraryLinkSpec deps1 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.SHARED), "mylib");
    CMakeLibraryLinkSpec deps2 = CMakeLibraryLinkSpec.Init.create(
        Map.of(CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.STATIC), "mylib");
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

  @Test
  void testEquals_sameObject() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "mylib");
    assertEquals(deps, deps);
  }

  @Test
  void testEquals_notInstanceOf() {
    CMakeLibraryLinkSpec deps = CMakeLibraryLinkSpec.Init.create(Map.of(), "mylib");
    assertNotEquals(deps, "not a link spec");
  }
}
