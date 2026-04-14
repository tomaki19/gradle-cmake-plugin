/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.helper.MockCMakePackage;

class CMakeResolvedPackageTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    assertNotNull(resolvedPackage);
    assertEquals("test-package", resolvedPackage.getName());
  }

  @Test
  void testGetProperties() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    assertNotNull(resolvedPackage.getProperties());
    assertTrue(resolvedPackage.getProperties().isEmpty());
  }

  @Test
  void testGetComponents() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    assertNotNull(resolvedPackage.getComponents());
    assertTrue(resolvedPackage.getComponents().isEmpty());
  }

  @Test
  void testIsModuleMode() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    assertFalse(resolvedPackage.isModuleMode());
  }

  @Test
  void testGetTargetPrefixAndResolvedPackage() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    CMakeResolvedPackageDependency dep = new CMakeResolvedPackageDependency("component",
        resolvedPackage, Optional.of("custom-prefix"));
    assertEquals("custom-prefix", dep.getTargetPrefix());
    assertEquals(resolvedPackage, dep.getResolvedPackage());
  }

  @Test
  void testGetTargetPrefixDefault() {
    final Project project = ProjectBuilder.builder().build();
    final CMakePackage pkg = new MockCMakePackage("test-package", project.getObjects());

    CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    CMakeResolvedPackageDependency dep = new CMakeResolvedPackageDependency("component",
        resolvedPackage, Optional.empty());
    assertEquals("test-package", dep.getTargetPrefix());
  }

}
