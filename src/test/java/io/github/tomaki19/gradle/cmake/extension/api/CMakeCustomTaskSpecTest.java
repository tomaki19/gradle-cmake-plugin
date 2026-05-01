/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeCustomTaskSpecTest {

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
  }

  // --- empty state ---

  @Test
  void hasNoToolchains_whenEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertTrue(spec.hasNoToolchains());
  }

  @Test
  void hasNoToolchains_whenNotEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("gcc")));
    assertFalse(spec.hasNoToolchains());
  }

  @Test
  void hasNoBuildConfigs_whenEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertTrue(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoBuildConfigs_whenNotEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("release")));
    assertFalse(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoComponents_whenEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertTrue(spec.hasNoComponents());
  }

  @Test
  void hasNoComponents_whenNotEmpty() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("myLib")));
    assertFalse(spec.hasNoComponents());
  }

  // --- matchesToolchain ---

  @Test
  void matchesToolchain_byName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("gcc")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("gcc");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_byAll() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("*")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("gcc")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertFalse(spec.matchesToolchain(toolchain));
  }

  // --- matchesBuildConfig ---

  @Test
  void matchesBuildConfig_byValue() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("release")));
    assertTrue(spec.matchesBuildConfig("release"));
  }

  @Test
  void matchesBuildConfig_byAll() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("*")));
    assertTrue(spec.matchesBuildConfig("debug"));
  }

  @Test
  void matchesBuildConfig_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("release")));
    assertFalse(spec.matchesBuildConfig("debug"));
  }

  // --- matchesLibrary ---

  @Test
  void matchesLibrary_byName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("myLib")));
    final CMakeResolvedLibrary library = resolvedLibrary("myLib", CMakeLinkVariant.STATIC);
    assertTrue(spec.matchesLibrary(library));
  }

  @Test
  void matchesLibrary_byAll() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("anyLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byLibraries() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*library")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byInterfaces() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*interface")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byShared() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*shared")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byStatic() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*static")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("other")));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  @Test
  void matchesLibrary_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  // --- matchesApplication ---

  @Test
  void matchesApplication_byName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("myApp")));
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byAll() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*")));
    assertTrue(spec.matchesApplication(resolvedApplication("anyApp")));
  }

  @Test
  void matchesApplication_byApplications() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*application")));
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("other")));
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byLibraries_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*library")));
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  // --- matchesTest ---

  @Test
  void matchesTest_byName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("myTest")));
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byAll() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*")));
    assertTrue(spec.matchesTest(resolvedTest("anyTest")));
  }

  @Test
  void matchesTest_byTests() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*test")));
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("other")));
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byApplications_noMatch() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("components", Set.of("*application")));
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  // --- equals / hashCode ---

  @Test
  void equals_sameInstance() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertEquals(spec, spec);
  }

  @Test
  void equals_emptySpecs() {
    assertEquals(new CMakeExecTaskSpec(Map.of()), new CMakeExecTaskSpec(Map.of()));
  }

  @Test
  void equals_sameContent() {
    final CMakeExecTaskSpec a = new CMakeExecTaskSpec(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), "components",
            Set.of("myLib")));
    final CMakeExecTaskSpec b = new CMakeExecTaskSpec(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), "components",
            Set.of("myLib")));
    assertEquals(a, b);
  }

  @Test
  void equals_differentToolchains() {
    final CMakeExecTaskSpec a = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("gcc")));
    final CMakeExecTaskSpec b = new CMakeExecTaskSpec(Map.of("toolchains", Set.of("clang")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentBuildConfigs() {
    final CMakeExecTaskSpec a = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("release")));
    final CMakeExecTaskSpec b = new CMakeExecTaskSpec(Map.of("buildConfigs", Set.of("debug")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentComponents() {
    final CMakeExecTaskSpec a = new CMakeExecTaskSpec(Map.of("components", Set.of("myLib")));
    final CMakeExecTaskSpec b = new CMakeExecTaskSpec(Map.of("components", Set.of("otherLib")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_notInstanceOf() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertNotEquals(spec, "not a spec");
  }

  @Test
  void hashCode_equalSpecs() {
    final CMakeExecTaskSpec a = new CMakeExecTaskSpec(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), "components",
            Set.of("myLib")));
    final CMakeExecTaskSpec b = new CMakeExecTaskSpec(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), "components",
            Set.of("myLib")));
    assertEquals(a.hashCode(), b.hashCode());
  }

  // --- helpers ---

  private CMakeResolvedToolchain resolvedToolchain(final String name) {
    return new CMakeResolvedToolchain(new MockCMakeToolchain(name, project.getObjects()));
  }

  private CMakeResolvedLibrary resolvedLibrary(final String name, final CMakeLinkVariant variant) {
    return new CMakeResolvedLibrary(new MockCMakeLibrary(name, project.getObjects()), variant, false);
  }

  private CMakeResolvedApplication resolvedApplication(final String name) {
    return new CMakeResolvedApplication(new MockCMakeApplication(name, project.getObjects()), false);
  }

  private CMakeResolvedTest resolvedTest(final String name) {
    return new CMakeResolvedTest(new MockCMakeTest(name, project.getObjects()), false);
  }
}
