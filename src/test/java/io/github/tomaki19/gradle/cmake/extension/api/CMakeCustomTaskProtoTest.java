/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeCustomTaskProtoTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();

    // Create a mock toolchain
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create a custom task proto
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");

    // Verify properties
    assertEquals("test-task", proto.getName());
    assertEquals("test-toolchain", proto.getToolchainName());
    assertEquals("debug", proto.getBuildConfig());
  }

  @Test
  void testGetters() {
    final Project project = ProjectBuilder.builder().build();

    // Create a mock toolchain
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create a custom task proto
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");

    // Verify all getters work correctly
    assertEquals("test-task", proto.getName());
    assertEquals("test-toolchain", proto.getToolchainName());
    assertEquals("debug", proto.getBuildConfig());
  }

  @Test
  void testEqualsAndHashCode() {
    final Project project = ProjectBuilder.builder().build();

    // Create mock toolchains
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create two protos with same values
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");

    // Verify equals and hashCode work correctly
    assertEquals(proto1, proto2);
    assertEquals(proto1.hashCode(), proto2.hashCode());
  }

  @Test
  void testEqualsWithDifferentName() {
    final Project project = ProjectBuilder.builder().build();

    // Create mock toolchains
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create two protos with different names
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task1", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task2", toolchain2, "debug");

    // Verify they are not equal
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithDifferentToolchainName() {
    final Project project = ProjectBuilder.builder().build();

    // Create mock toolchains
    CMakeToolchain toolchain1 = new MockCMakeToolchain("toolchain1", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("toolchain2", project.getObjects());

    // Create two protos with different toolchain names
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");

    // Verify they are not equal
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithDifferentBuildConfig() {
    final Project project = ProjectBuilder.builder().build();

    // Create mock toolchains
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create two protos with different build configs
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "release");

    // Verify they are not equal
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithNull() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create a proto
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");

    // Verify equals with null returns false
    assertFalse(proto.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());

    // Create a proto
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");

    // Verify equals with different class returns false
    assertFalse(proto.equals("not a proto"));
  }
}
