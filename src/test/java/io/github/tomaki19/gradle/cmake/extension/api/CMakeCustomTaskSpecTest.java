/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertTrue(spec.hasNoToolchains());
  }

  @Test
  void hasNoToolchains_whenNotEmpty() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("gcc")), "myExec");
    assertFalse(spec.hasNoToolchains());
  }

  @Test
  void hasNoBuildConfigs_whenEmpty() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertTrue(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoBuildConfigs_whenNotEmpty() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("release")), "myExec");
    assertFalse(spec.hasNoBuildConfigs());
  }

  @Test
  void hasNoComponents_whenEmpty() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertTrue(spec.hasNoComponents());
  }

  @Test
  void hasNoComponents_whenNotEmpty() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("myLib")), "myExec");
    assertFalse(spec.hasNoComponents());
  }

  // --- matchesToolchain ---

  @Test
  void matchesToolchain_byName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("gcc")), "myExec");
    final CMakeResolvedToolchain toolchain = resolvedToolchain("gcc");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_byAll() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("*")), "myExec");
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertTrue(spec.matchesToolchain(toolchain));
  }

  @Test
  void matchesToolchain_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("gcc")), "myExec");
    final CMakeResolvedToolchain toolchain = resolvedToolchain("clang");
    assertFalse(spec.matchesToolchain(toolchain));
  }

  // --- matchesBuildConfig ---

  @Test
  void matchesBuildConfig_byValue() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("release")), "myExec");
    assertTrue(spec.matchesBuildConfig("release"));
  }

  @Test
  void matchesBuildConfig_byAll() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("*")), "myExec");
    assertTrue(spec.matchesBuildConfig("debug"));
  }

  @Test
  void matchesBuildConfig_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("release")), "myExec");
    assertFalse(spec.matchesBuildConfig("debug"));
  }

  // --- matchesLibrary ---

  @Test
  void matchesLibrary_byName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("myLib")), "myExec");
    final CMakeResolvedLibrary library = resolvedLibrary("myLib", CMakeLinkVariant.STATIC);
    assertTrue(spec.matchesLibrary(library));
  }

  @Test
  void matchesLibrary_byAll() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*")), "myExec");
    assertTrue(spec.matchesLibrary(resolvedLibrary("anyLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byLibraries() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*library")), "myExec");
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byInterfaces() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*interface")), "myExec");
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
  }

  @Test
  void matchesLibrary_byShared() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*shared")), "myExec");
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_byStatic() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init
        .create(Map.of(CMakeExecTaskSpec.PREFIX, "myExec", CMakeExecTaskSpec.COMPONENTS, Set.of("*static")));
    assertTrue(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.SHARED)));
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.INTERFACE)));
  }

  @Test
  void matchesLibrary_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("other")), "myExec");
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  @Test
  void matchesLibrary_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertFalse(spec.matchesLibrary(resolvedLibrary("myLib", CMakeLinkVariant.STATIC)));
  }

  // --- matchesApplication ---

  @Test
  void matchesApplication_byName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("myApp")), "myExec");
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byAll() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*")), "myExec");
    assertTrue(spec.matchesApplication(resolvedApplication("anyApp")));
  }

  @Test
  void matchesApplication_byApplications() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*application")),
        "myExec");
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("other")), "myExec");
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byExecutables() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*executable")), "myExec");
    assertTrue(spec.matchesApplication(resolvedApplication("myApp")));
  }

  @Test
  void matchesApplication_byLibraries_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*library")), "myExec");
    assertFalse(spec.matchesApplication(resolvedApplication("myApp")));
  }

  // --- matchesTest ---

  @Test
  void matchesTest_byName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("myTest")), "myExec");
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byAll() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*")), "myExec");
    assertTrue(spec.matchesTest(resolvedTest("anyTest")));
  }

  @Test
  void matchesTest_byTests() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*test")), "myExec");
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_noMatch_wrongName() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("other")), "myExec");
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_emptyComponents_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byExecutables() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*executable")), "myExec");
    assertTrue(spec.matchesTest(resolvedTest("myTest")));
  }

  @Test
  void matchesTest_byApplications_noMatch() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("*application")),
        "myExec");
    assertFalse(spec.matchesTest(resolvedTest("myTest")));
  }

  // --- validateType / validateMandatory ---

  @Test
  void validateType_wrongType_throws() {
    assertThrows(CMakeApiException.class,
        () -> CMakeExecTaskSpec.Init.create(Map.of("toolchains", "notACollection"), "myExec"));
  }

  @Test
  void validateMandatory_keyPresent_noThrow() {
    CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
  }

  // --- equals / hashCode ---

  @Test
  void equals_sameInstance() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertEquals(spec, spec);
  }

  @Test
  void equals_emptySpecs() {
    assertEquals(CMakeExecTaskSpec.Init.create(Map.of(), "myExec"),
        CMakeExecTaskSpec.Init.create(Map.of(), "myExec"));
  }

  @Test
  void equals_sameContent() {
    final CMakeExecTaskSpec a = CMakeExecTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), CMakeExecTaskSpec.COMPONENTS,
            Set.of("myLib")),
        "myExec");
    final CMakeExecTaskSpec b = CMakeExecTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), CMakeExecTaskSpec.COMPONENTS,
            Set.of("myLib")),
        "myExec");
    assertEquals(a, b);
  }

  @Test
  void equals_differentToolchains() {
    final CMakeExecTaskSpec a = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("gcc")), "myExec");
    final CMakeExecTaskSpec b = CMakeExecTaskSpec.Init.create(Map.of("toolchains", Set.of("clang")), "myExec");
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentBuildConfigs() {
    final CMakeExecTaskSpec a = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("release")), "myExec");
    final CMakeExecTaskSpec b = CMakeExecTaskSpec.Init.create(Map.of("buildConfigs", Set.of("debug")), "myExec");
    assertNotEquals(a, b);
  }

  @Test
  void equals_differentComponents() {
    final CMakeExecTaskSpec a = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("myLib")), "myExec");
    final CMakeExecTaskSpec b = CMakeExecTaskSpec.Init.create(Map.of(CMakeExecTaskSpec.COMPONENTS, Set.of("otherLib")), "myExec");
    assertNotEquals(a, b);
  }

  @Test
  void equals_notInstanceOf() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myExec");
    assertNotEquals(spec, "not a spec");
  }

  @Test
  void hashCode_equalSpecs() {
    final CMakeExecTaskSpec a = CMakeExecTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), CMakeExecTaskSpec.COMPONENTS,
            Set.of("myLib")),
        "myExec");
    final CMakeExecTaskSpec b = CMakeExecTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc"), "buildConfigs", Set.of("release"), CMakeExecTaskSpec.COMPONENTS,
            Set.of("myLib")),
        "myExec");
    assertEquals(a.hashCode(), b.hashCode());
  }

  // --- helpers ---

  private CMakeResolvedToolchain resolvedToolchain(final String name) {
    return new CMakeResolvedToolchain(new MockCMakeToolchain(name, project.getObjects()));
  }

  private CMakeResolvedLibrary resolvedLibrary(final String name, final CMakeLinkVariant variant) {
    return new CMakeResolvedLibrary(new MockCMakeLibrary(name, project.getObjects()), variant, false, "unspecified");
  }

  private CMakeResolvedApplication resolvedApplication(final String name) {
    return new CMakeResolvedApplication(new MockCMakeApplication(name, project.getObjects()), false, "unspecified");
  }

  private CMakeResolvedTest resolvedTest(final String name) {
    return new CMakeResolvedTest(new MockCMakeTest(name, project.getObjects()), false, "unspecified");
  }
}
