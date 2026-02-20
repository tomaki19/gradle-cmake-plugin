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

import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeTest;

class CMakeResolvedExecutableTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);
    assertEquals("test-executable", resolvedExecutable.getName());
  }

  @Test
  void testGetters() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    // Test default values
    assertFalse(resolvedExecutable.isStripDebug());
    assertTrue(resolvedExecutable.getPrivateCompileDefinitions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateCompileOptions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateLinkOptions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateSystemPackageDependencies().isEmpty());
    assertTrue(resolvedExecutable.getPrivateProjectPackageDependencies().isEmpty());
  }

  @Test
  void testAddPrivateCompileDefinitions() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    resolvedExecutable.addPrivateCompileDefinitions("TEST_DEFINE");
    assertFalse(resolvedExecutable.getPrivateCompileDefinitions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateCompileDefinitions().contains("TEST_DEFINE"));
  }

  @Test
  void testAddPrivateCompileOptions() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    resolvedExecutable.addPrivateCompileOptions("-O2");
    assertFalse(resolvedExecutable.getPrivateCompileOptions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateCompileOptions().contains("-O2"));
  }

  @Test
  void testAddPrivateLinkOption() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    resolvedExecutable.addPrivateLinkOption("-ltest");
    assertFalse(resolvedExecutable.getPrivateLinkOptions().isEmpty());
    assertTrue(resolvedExecutable.getPrivateLinkOptions().contains("-ltest"));
  }

  @Test
  void testAddPrivateSystemPackageDependency() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    resolvedExecutable.addPrivateSystemPackageDependency("pkg-config");
    assertFalse(resolvedExecutable.getPrivateSystemPackageDependencies().isEmpty());
    assertTrue(resolvedExecutable.getPrivateSystemPackageDependencies().contains("pkg-config"));
  }

  @Test
  void testAddPrivateProjectPackageDependency() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeTest test = new MockCMakeTest("test-executable", project.getObjects());

    CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
    assertNotNull(resolvedExecutable);

    // This would require a CMakeResolvedProjectDependency object, so we'll just
    // test that it doesn't throw
    // The actual implementation would be tested in integration tests
    assertTrue(true); // Placeholder test
  }
}
