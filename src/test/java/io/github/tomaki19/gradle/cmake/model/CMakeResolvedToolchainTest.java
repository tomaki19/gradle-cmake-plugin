/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
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
    assertEquals(4, resolvedToolchain.getBuildConfigs().size());
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

  @Test
  void testGetEnvironment() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain.getEnvironment());
  }

  @Test
  void testGetOperatingSystem() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain.getOperatingSystem());
  }

  @Test
  void testGetGenerator() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain.getGenerator());
  }

  @Test
  void testGetEnvironmentFile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain.getEnvironmentFile());
    assertFalse(resolvedToolchain.getEnvironmentFile().isPresent());
  }

  @Test
  void testGetToolchainFile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());

    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertNotNull(resolvedToolchain.getToolchainFile());
    assertFalse(resolvedToolchain.getToolchainFile().isPresent());
  }

  @Test
  void testHasInterfaceLibraries_true() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(
        new MockCMakeToolchain("TestToolchain", project.getObjects()));
    resolvedToolchain.addInterfaceLibrary(
        new CMakeResolvedLibrary(new MockCMakeLibrary("myLib", project.getObjects()), CMakeLinkVariant.INTERFACE,
            false));
    assertTrue(resolvedToolchain.hasInterfaceLibraries());
  }
}
