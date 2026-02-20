/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeResolvedToolchainTest {

  @Test
  void testResolvedToolchainCreation() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain);
    assertEquals("TestToolchain", resolvedToolchain.getName());
  }

  @Test
  void testResolvedToolchainWithEmptyBuildConfigs() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain);
    assertEquals(2, resolvedToolchain.getBuildConfigs().size());
  }

  @Test
  void testHasBinaries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertFalse(resolvedToolchain.hasBinaries());
  }

  @Test
  void testHasApplications() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertFalse(resolvedToolchain.hasApplications());
  }

  @Test
  void testHasTests() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertFalse(resolvedToolchain.hasTests());
  }

  @Test
  void testHasInterfaceLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertFalse(resolvedToolchain.hasInterfaceLibraries());
  }

  @Test
  void testHasBinaryLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertFalse(resolvedToolchain.hasBinaryLibraries());
  }
}
