/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;

class CMakeApplicationTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    assertNotNull(application);
  }

  @Test
  void testGetName() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    assertEquals("test", application.getName());
  }

  @Test
  void testGetPrivateCompile() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    assertNotNull(application.getCompiling());
  }

  @Test
  void testGetPrivateLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    assertNotNull(application.getLinking());
  }

  @Test
  void testGetStripDebug() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    assertNotNull(application.getStripDebug());
  }

  @Test
  void testHeadersAction() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    application.headers(headers -> {
      assertNotNull(headers);
    });
  }

  @Test
  void testSourcesAction() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("test", project.getObjects());
    application.sources(sources -> {
      assertNotNull(sources);
    });
  }
}
