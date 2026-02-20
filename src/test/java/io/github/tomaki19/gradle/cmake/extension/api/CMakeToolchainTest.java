/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeToolchainTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    assertNotNull(toolchain);
  }

  @Test
  void testCompareTo() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain1 = new MockCMakeToolchain("test1", project.getObjects());
    final CMakeToolchain toolchain2 = new MockCMakeToolchain("test2", project.getObjects());

    // Test comparison
    int result = toolchain1.compareTo(toolchain2);
    assertTrue(result < 0);
  }

  @Test
  void testSetOperatingSystem() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test setting operating system
    toolchain.getOperatingSystem().set(OperatingSystem.MAC_OS);
    assertEquals(OperatingSystem.MAC_OS, toolchain.getOperatingSystem().get());
  }

  @Test
  void testSetBuildConfigs() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test setting build configs with collection
    toolchain.buildConfigs("debug", "release", "custom");
    assertEquals(3, toolchain.getBuildConfigs().size());
  }

  @Test
  void testSetEnvironment() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test setting environment
    toolchain.getEnvironment().put("key", "value");
    assertEquals(1, toolchain.getEnvironment().get().size());
  }

  @Test
  void testSetEnvironmentFile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test setting environment file
    final RegularFile testFile = project.getLayout().getProjectDirectory().file("test.txt");
    toolchain.getEnvironmentFile().set(testFile);
    assertEquals(testFile, toolchain.getEnvironmentFile().get());
  }

  @Test
  void testSetToolchainFile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test setting toolchain file
    final RegularFile testFile = project.getLayout().getProjectDirectory().file("test.txt");
    toolchain.getToolchainFile().set(testFile);
    assertEquals(testFile, toolchain.getToolchainFile().get());
  }
}
