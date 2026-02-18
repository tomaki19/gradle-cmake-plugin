/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.gradle.internal.os.OperatingSystem;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeToolchainTest {

  @Test
  void testConstructor() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    assertNotNull(toolchain);
  }

  @Test
  void testOperatingSystem() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test default value
    assertEquals(OperatingSystem.current(), toolchain.getOperatingSystem());

    // Test setting value
    toolchain.setOperatingSystem(OperatingSystem.LINUX);
    assertEquals(OperatingSystem.LINUX, toolchain.getOperatingSystem());
  }

  @Test
  void testBuildConfigs() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test default value
    Collection<String> buildConfigs = toolchain.getBuildConfigs();
    assertNotNull(buildConfigs);
    assertFalse(buildConfigs.isEmpty());
    assertEquals(2, buildConfigs.size());

    // Test setting value with collection
    toolchain.setBuildConfigs(Arrays.asList("debug", "release", "custom"));
    assertEquals(3, toolchain.getBuildConfigs().size());

    // Test with varargs
    toolchain.buildConfigs("test1", "test2");
    assertEquals(2, toolchain.getBuildConfigs().size());
  }

  @Test
  void testEnvironment() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test default value
    Map<String, String> environment = toolchain.getEnvironment();
    assertNotNull(environment);
    assertTrue(environment.isEmpty());

    // Test setting value
    toolchain.setEnvironment(Collections.singletonMap("key", "value"));
    assertFalse(toolchain.getEnvironment().isEmpty());
    assertEquals(1, toolchain.getEnvironment().size());
  }

  @Test
  void testEnvironmentFile() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test default value
    assertFalse(toolchain.getEnvironmentFile().isPresent());

    // Test setting value
    File testFile = new File("test.txt");
    toolchain.setEnvironmentFile(testFile);
    assertTrue(toolchain.getEnvironmentFile().isPresent());
    assertEquals(testFile, toolchain.getEnvironmentFile().get());
  }

  @Test
  void testToolchainFile() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test default value
    assertFalse(toolchain.getToolchainFile().isPresent());

    // Test setting value
    File testFile = new File("test.txt");
    toolchain.setToolchainFile(testFile);
    assertTrue(toolchain.getToolchainFile().isPresent());
    assertEquals(testFile, toolchain.getToolchainFile().get());
  }

  @Test
  void testCompareTo() {
    final CMakeToolchain toolchain1 = new MockCMakeToolchain("test1");

    final CMakeToolchain toolchain2 = new MockCMakeToolchain("test2");

    // Test comparison
    int result = toolchain1.compareTo(toolchain2);
    assertTrue(result < 0);
  }

  @Test
  void testSetOperatingSystem() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test setting operating system
    toolchain.setOperatingSystem(OperatingSystem.MAC_OS);
    assertEquals(OperatingSystem.MAC_OS, toolchain.getOperatingSystem());
  }

  @Test
  void testSetBuildConfigs() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test setting build configs with collection
    toolchain.setBuildConfigs(Arrays.asList("debug", "release", "custom"));
    assertEquals(3, toolchain.getBuildConfigs().size());
  }

  @Test
  void testSetEnvironment() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test setting environment
    toolchain.setEnvironment(Collections.singletonMap("key", "value"));
    assertFalse(toolchain.getEnvironment().isEmpty());
    assertEquals(1, toolchain.getEnvironment().size());
  }

  @Test
  void testSetEnvironmentFile() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test setting environment file
    File testFile = new File("test.txt");
    toolchain.setEnvironmentFile(testFile);
    assertTrue(toolchain.getEnvironmentFile().isPresent());
    assertEquals(testFile, toolchain.getEnvironmentFile().get());
  }

  @Test
  void testSetToolchainFile() {
    final CMakeToolchain toolchain = new MockCMakeToolchain("test");

    // Test setting toolchain file
    File testFile = new File("test.txt");
    toolchain.setToolchainFile(testFile);
    assertTrue(toolchain.getToolchainFile().isPresent());
    assertEquals(testFile, toolchain.getToolchainFile().get());
  }
}
