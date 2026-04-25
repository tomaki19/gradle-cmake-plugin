/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Map;

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
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertTrue(spec.hasNoToolchains());
  }

  @Test
  void hasNoToolchains_whenNotEmpty() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("gcc")));
    assertFalse(spec.hasNoToolchains());
  }

  @Test
  void hasNoBuildConfigs_whenEmpty() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertTrue(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoBuildConfigs_whenNotEmpty() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("release")));
    assertFalse(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoComponents_whenEmpty() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertTrue(spec.hasNoComponents());
  }

  @Test
  void hasNoComponents_whenNotEmpty() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("myLib")));
    assertFalse(spec.hasNoComponents());
  }

  // --- matchesToolchain ---

  @Test
  void matchesToolchain_byName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("gcc")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("gcc");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_byAll() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("*")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("gcc")));
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertFalse(spec.matchesToolchain(toolchain));
  }

  // --- matchesBuildConfig ---

  @Test
  void matchesBuildConfig_byValue() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("release")));
    assertTrue(spec.matchesBuildConfig("release"));
  }

  @Test
  void matchesBuildConfig_byAll() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("*")));
    assertTrue(spec.matchesBuildConfig("debug"));
  }

  @Test
  void matchesBuildConfig_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("release")));
    assertFalse(spec.matchesBuildConfig("debug"));
  }

  // --- matchesLibrary ---

  @Test
  void matchesLibrary_byName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("myLib")));
    final CMakeResolvedLibrary library = resolvedLibrary("myLib", CMakeLinkVariant.STATIC);
    assertTrue(spec.matchesLibrary(library));
  }

  @Test
  void matchesLibrary_byAll() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("anyLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byLibraries() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*library")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byInterfaces() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*interface")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byShared() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*shared")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byStatic() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*static")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_noMatch_wrongName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("other")));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  @Test
  void matchesLibrary_emptyComponents_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  // --- matchesApplication ---

  @Test
  void matchesApplication_byName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("myApp")));
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byAll() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*")));
    assertTrue(spec.matchesApplication(resolvedApplication("anyApp")));
  }

  @Test
  void matchesApplication_byApplications() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*application")));
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_noMatch_wrongName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("other")));
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_emptyComponents_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byLibraries_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*library")));
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  // --- matchesTest ---

  @Test
  void matchesTest_byName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("myTest")));
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byAll() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*")));
    assertTrue(spec.matchesTest(resolvedTest("anyTest")));
  }

  @Test
  void matchesTest_byTests() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*test")));
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_noMatch_wrongName() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("other")));
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_emptyComponents_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byApplications_noMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("*application")));
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  // --- equals / hashCode ---

  @Test
  void equals_sameInstance() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertEquals(spec, spec);
  }

  @Test
  void equals_emptySpecs() {
    assertEquals(new CMakeCustomTaskSpec(Map.of()), new CMakeCustomTaskSpec(Map.of()));
  }

  @Test
  void equals_sameContent() {
    final CMakeCustomTaskSpec a = new CMakeCustomTaskSpec(
        Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs", Arrays.asList("release"), "components",
            Arrays.asList("myLib")));
    final CMakeCustomTaskSpec b = new CMakeCustomTaskSpec(
        Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs", Arrays.asList("release"), "components",
            Arrays.asList("myLib")));
    assertEquals(a, b);
  }

  @Test
  void equals_differentToolchains() {
    final CMakeCustomTaskSpec a = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("gcc")));
    final CMakeCustomTaskSpec b = new CMakeCustomTaskSpec(Map.of("toolchains", Arrays.asList("clang")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentBuildConfigs() {
    final CMakeCustomTaskSpec a = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("release")));
    final CMakeCustomTaskSpec b = new CMakeCustomTaskSpec(Map.of("buildConfigs", Arrays.asList("debug")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentComponents() {
    final CMakeCustomTaskSpec a = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("myLib")));
    final CMakeCustomTaskSpec b = new CMakeCustomTaskSpec(Map.of("components", Arrays.asList("otherLib")));
    assertNotEquals(a, b);
  }

  @Test
  void equals_notInstanceOf() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec(Map.of());
    assertNotEquals(spec, "not a spec");
  }

  @Test
  void hashCode_equalSpecs() {
    final CMakeCustomTaskSpec a = new CMakeCustomTaskSpec(
        Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs", Arrays.asList("release"), "components",
            Arrays.asList("myLib")));
    final CMakeCustomTaskSpec b = new CMakeCustomTaskSpec(
        Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs", Arrays.asList("release"), "components",
            Arrays.asList("myLib")));
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
