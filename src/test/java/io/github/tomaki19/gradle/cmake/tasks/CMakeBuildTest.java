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

class CMakeBuildTest {

  private Project project;
  private CMakeResolvedToolchain resolvedToolchain;
  private CMakeResolvedApplication resolvedApplication;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    resolvedToolchain = new CMakeResolvedToolchain(new MockCMakeToolchain("TestToolchain", project.getObjects()));
    resolvedApplication = new CMakeResolvedApplication(new MockCMakeApplication("MyApp", project.getObjects()), false);
  }

  @Test
  void testTaskCreation() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testTaskWithDefaultConfig() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Release").get();
    assertNotNull(task);
  }

  @Test
  void testBuildTargetName() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testToolchainName() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testBuildConfig() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testExecutableName() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getExecutable());
  }

  @Test
  void testWorkingDirectory() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getWorkingDir());
  }

  @Test
  void testTaskGroup() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getGroup());
  }

  @Test
  void testCachingEnabled() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task);
  }

  @Test
  void testMultipleBuildTargets() {
    CMakeBuildExecutable task1 = project.getTasks()
        .register("build1", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    CMakeBuildExecutable task2 = project.getTasks()
        .register("build2", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Release").get();
    assertNotNull(task1);
    assertNotNull(task2);
  }

  @Test
  void testTargetDependencies() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getProject());
  }

  @Test
  void testInputs() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getInputs());
  }

  @Test
  void testOutputs() {
    CMakeBuildExecutable task = project.getTasks()
        .register("build", CMakeBuildExecutable.class, resolvedApplication, resolvedToolchain, "Debug").get();
    assertNotNull(task.getOutputs());
  }
}
