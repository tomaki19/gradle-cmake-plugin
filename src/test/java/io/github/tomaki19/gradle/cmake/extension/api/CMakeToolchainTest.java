/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeToolchainTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());
    assertNotNull(toolchain);
  }

  @Test
  void testCompareTo() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test1", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test2", project.getObjects());

    int result = toolchain1.compareTo(toolchain2);
    assertTrue(result < 0);
  }

  @Test
  void testSetOperatingSystem() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    toolchain.getOperatingSystem().set(org.gradle.internal.os.OperatingSystem.MAC_OS);
    assertEquals(org.gradle.internal.os.OperatingSystem.MAC_OS, toolchain.getOperatingSystem().get());
  }

  @Test
  void testSetBuildConfigs() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    toolchain.buildConfigs("debug", "release", "custom");
    assertEquals(3, toolchain.getBuildConfigs().size());
  }

  @Test
  void testSetEnvironment() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    toolchain.getEnvironment().put("key", "value");
    assertEquals(1, toolchain.getEnvironment().get().size());
  }

  @Test
  void testSetEnvironmentFile() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    org.gradle.api.file.RegularFile testFile = project.getLayout().getProjectDirectory().file("test.txt");
    toolchain.getEnvironmentFile().set(testFile);
    assertEquals(testFile, toolchain.getEnvironmentFile().get());
  }

  @Test
  void testSetToolchainFile() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    org.gradle.api.file.RegularFile testFile = project.getLayout().getProjectDirectory().file("test.txt");
    toolchain.getToolchainFile().set(testFile);
    assertEquals(testFile, toolchain.getToolchainFile().get());
  }

  @Test
  void testLibrariesAction() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test that the libraries action can be executed
    toolchain.libraries(libs -> {
      // This should not throw any exceptions
      assertNotNull(libs);
    });
  }

  @Test
  void testApplicationsAction() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test that the applications action can be executed
    toolchain.applications(apps -> {
      // This should not throw any exceptions
      assertNotNull(apps);
    });
  }

  @Test
  void testTestsAction() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    // Test that the tests action can be executed
    toolchain.tests(tests -> {
      // This should not throw any exceptions
      assertNotNull(tests);
    });
  }

  @Test
  void testHashCode() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test", project.getObjects());

    assertEquals(toolchain1.hashCode(), toolchain2.hashCode());
  }

  @Test
  void testEquals() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test", project.getObjects());

    assertEquals(toolchain1, toolchain2);
  }

  @Test
  void testEqualsWithDifferentName() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test1", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test2", project.getObjects());

    assertNotEquals(toolchain1, toolchain2);
  }

  @Test
  void testEqualsWithNull() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    assertFalse(toolchain.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test", project.getObjects());

    assertFalse(toolchain.equals("not a toolchain"));
  }
}
