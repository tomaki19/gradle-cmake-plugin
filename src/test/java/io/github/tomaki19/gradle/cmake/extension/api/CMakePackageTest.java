/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakePackage;

class CMakePackageTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    assertNotNull(pkg);
  }

  @Test
  void testGetConfigMode() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    assertFalse(pkg.getModuleMode().isPresent());
  }

  @Test
  void testSetConfigMode() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    pkg.getModuleMode().set(true);
    assertEquals(Boolean.TRUE, pkg.getModuleMode().get());
  }

  @Test
  void testGetTargetPrefix() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    assertFalse(pkg.getTargetPrefix().isPresent());
  }

  @Test
  void testSetTargetPrefix() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    pkg.getTargetPrefix().set("prefix");
    assertEquals("prefix", pkg.getTargetPrefix().get());
  }

  @Test
  void testGetComponents() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    assertEquals(0, pkg.getComponents().get().size());
  }

  @Test
  void testSetComponents() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    pkg.getComponents().set(Arrays.asList("component1", "component2"));
    assertEquals(2, pkg.getComponents().get().size());
  }

  @Test
  void testGetProperties() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    assertEquals(0, pkg.getProperties().get().size());
  }

  @Test
  void testSetProperties() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test", project.getObjects());
    Map<String, String> props = new HashMap<>();
    props.put("key1", "value1");
    props.put("key2", "value2");
    pkg.getProperties().putAll(props);
    assertEquals(2, pkg.getProperties().get().size());
  }

}
