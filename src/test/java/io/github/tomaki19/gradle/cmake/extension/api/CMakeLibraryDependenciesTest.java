/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

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
    deps.variant(CMakeLinkVariant.STATIC);
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkShared() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.variant(CMakeLinkVariant.SHARED);
    assertEquals("shared", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testGetLinkInterface() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    deps.variant(CMakeLinkVariant.INTERFACE);
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

  @Test
  void testEqualsAndHashCode() {
    CMakeLibraryDependencies deps1 = new CMakeLibraryDependencies("mylib");
    deps1.forBuildVariant(CMakeBuildVariant.SHARED);
    deps1.variant(CMakeLinkVariant.STATIC);
    deps1.visibility(CMakeVisibility.PUBLIC);

    CMakeLibraryDependencies deps2 = new CMakeLibraryDependencies("mylib");
    deps2.forBuildVariant(CMakeBuildVariant.SHARED);
    deps2.variant(CMakeLinkVariant.STATIC);
    deps2.visibility(CMakeVisibility.PUBLIC);

    assertEquals(deps1, deps2);
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEqualsAndHashCodeWithDifferentBuildVariant() {
    CMakeLibraryDependencies deps1 = new CMakeLibraryDependencies("mylib");
    deps1.forBuildVariant(CMakeBuildVariant.SHARED);

    CMakeLibraryDependencies deps2 = new CMakeLibraryDependencies("mylib");
    deps2.forBuildVariant(CMakeBuildVariant.STATIC);

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsAndHashCodeWithDifferentLinkVariant() {
    CMakeLibraryDependencies deps1 = new CMakeLibraryDependencies("mylib");
    deps1.variant(CMakeLinkVariant.STATIC);

    CMakeLibraryDependencies deps2 = new CMakeLibraryDependencies("mylib");
    deps2.variant(CMakeLinkVariant.SHARED);

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsAndHashCodeWithDifferentVisibility() {
    CMakeLibraryDependencies deps1 = new CMakeLibraryDependencies("mylib");
    deps1.visibility(CMakeVisibility.PUBLIC);

    CMakeLibraryDependencies deps2 = new CMakeLibraryDependencies("mylib");
    deps2.visibility(CMakeVisibility.PRIVATE);

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsAndHashCodeWithDifferentNames() {
    CMakeLibraryDependencies deps1 = new CMakeLibraryDependencies("mylib1");
    CMakeLibraryDependencies deps2 = new CMakeLibraryDependencies("mylib2");

    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    assertNotEquals(null, deps);
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeLibraryDependencies deps = new CMakeLibraryDependencies("mylib");
    assertNotEquals(new Object(), deps);
  }
}
