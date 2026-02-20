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
import org.gradle.api.file.RegularFile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;

class CMakeCustomTaskProtoTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    assertNotNull(proto);
  }

  @Test
  void testGetters() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    assertEquals("test-task", proto.getName());
    assertEquals("test-toolchain", proto.getToolchainName());
    assertEquals("debug", proto.getBuildConfig());
  }

  @Test
  void testGetEnvironmentFileWithNull() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    // When no environment file is set, getEnvironmentFile should return empty Optional
    assertFalse(proto.getEnvironmentFile().isPresent());
  }

  @Test
  void testGetEnvironmentFileWithFile() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    
    // Set an environment file
    RegularFile testFile = project.getLayout().getProjectDirectory().file("test.txt");
    toolchain.getEnvironmentFile().set(testFile);
    
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    assertTrue(proto.getEnvironmentFile().isPresent());
    assertEquals(testFile, proto.getEnvironmentFile().get());
  }

  @Test
  void testEqualsAndHashCode() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");
    
    assertEquals(proto1, proto2);
    assertEquals(proto1.hashCode(), proto2.hashCode());
  }

  @Test
  void testEqualsWithDifferentName() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task1", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task2", toolchain2, "debug");
    
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithDifferentToolchainName() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("toolchain1", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("toolchain2", project.getObjects());
    
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");
    
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithDifferentBuildConfig() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain1 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeToolchain toolchain2 = new MockCMakeToolchain("test-toolchain", project.getObjects());
    
    CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
    CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "release");
    
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEqualsWithNull() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    assertFalse(proto.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    final Project project = ProjectBuilder.builder().build();
    CMakeToolchain toolchain = new MockCMakeToolchain("test-toolchain", project.getObjects());
    CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
    
    assertFalse(proto.equals("not a proto"));
  }
}
