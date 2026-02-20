/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    final CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
    assertNotNull(resolvedPackage);
    assertEquals("test-package", resolvedPackage.getName());
  }

}
