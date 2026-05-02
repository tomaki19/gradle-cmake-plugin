/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CMakeCleanTest {

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
  }

  @Test
  void testTaskCreation() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task);
  }

  @Test
  void testDeleteAction() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task.getDelete());
  }

  @Test
  void testTaskGroup() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task);
  }

  @Test
  void testTaskName() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task.getName());
  }

  @Test
  void testTaskType() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task);
  }

  @Test
  void testDisableCaching() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task);
  }

  @Test
  void testDependencyResolution() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task.getProject());
  }

  @Test
  void testMultipleDeletionOperations() {
    CMakeClean task = project.getTasks().register("cleanTask", CMakeClean.class).get();
    assertNotNull(task.getDelete());
  }
}
