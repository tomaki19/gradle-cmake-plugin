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

import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeCheckTest {

  private Project project;
  private CMakeResolvedToolchain resolvedToolchain;
  private CMakeResolvedApplication resolvedApplication;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    resolvedToolchain = new CMakeResolvedToolchain(new MockCMakeToolchain("TestToolchain", project.getObjects()));
    resolvedApplication = new CMakeResolvedApplication(new MockCMakeApplication("MyApp", project.getObjects()), false, "unspecified");
  }

  @Test
  void testTaskCreation() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testTaskGroup() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getGroup());
  }

  @Test
  void testTaskName() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getName());
  }

  @Test
  void testExecutableName() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getExecutable());
  }

  @Test
  void testWorkingDirectory() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getWorkingDir());
  }

  @Test
  void testMultipleCheckOperations() {
    CMakeCheck task1 = project.getTasks()
        .register("checkDebug", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    CMakeCheck task2 = project.getTasks()
        .register("checkRelease", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Release").get();
    assertNotNull(task1);
    assertNotNull(task2);
  }

  @Test
  void testTargetDependencies() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getProject());
  }

  @Test
  void testInputs() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getInputs());
  }

  @Test
  void testOutputs() {
    CMakeCheck task = project.getTasks()
        .register("check", CMakeCheck.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getOutputs());
  }
}
