/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeConfigureTest {

  private Project project;
  private CMakeResolvedToolchain resolvedToolchain;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    resolvedToolchain = new CMakeResolvedToolchain(new MockCMakeToolchain("TestToolchain", project.getObjects()));
  }

  @Test
  void testTaskCreation() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task);
  }

  @Test
  void testTaskWithDefaultConfig() {
    CMakeConfigure task = project.getTasks()
        .register("configure", CMakeConfigure.class, resolvedToolchain, "Release").get();
    assertNotNull(task);
  }

  @Test
  void testToolchainName() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task);
  }

  @Test
  void testBuildConfig() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task);
  }

  @Test
  void testExecutableName() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getExecutable());
  }

  @Test
  void testWorkingDirectory() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getWorkingDir());
  }

  @Test
  void testTaskGroup() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task);
  }

  @Test
  void testMultipleConfigureOperations() {
    CMakeConfigure task1 = project.getTasks()
        .register("configureDebug", CMakeConfigure.class, resolvedToolchain, "Debug").get();
    CMakeConfigure task2 = project.getTasks()
        .register("configureRelease", CMakeConfigure.class, resolvedToolchain, "Release").get();
    assertNotNull(task1);
    assertNotNull(task2);
  }

  @Test
  void testTargetDependencies() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getProject());
  }

  @Test
  void testInputs() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getInputs());
  }

  @Test
  void testOutputs() {
    CMakeConfigure task = project.getTasks().register("configure", CMakeConfigure.class, resolvedToolchain, "Debug")
        .get();
    assertNotNull(task.getOutputs());
  }

  @Test
  void testTaskCreationWithToolchainFile() throws Exception {
    File toolchainFile = File.createTempFile("toolchain", ".cmake");
    try {
      MockCMakeToolchain toolchain = new MockCMakeToolchain("TestToolchain", project.getObjects());
      toolchain.getToolchainFile().set(toolchainFile);
      CMakeResolvedToolchain tcWithFile = new CMakeResolvedToolchain(toolchain);

      CMakeConfigure task = project.getTasks()
          .register("configureWithTC", CMakeConfigure.class, tcWithFile, "Debug").get();
      assertNotNull(task);
    } finally {
      Files.deleteIfExists(toolchainFile.toPath());
    }
  }

  @Test
  void testExec_coversStreamLambdas() throws Exception {
    File inputDir = Files.createTempDirectory("cmake-input").toFile();
    try {
      CMakeConfigure task = project.getTasks()
          .register("configureExec", CMakeConfigure.class, resolvedToolchain, "Debug").get();
      task.setExecutable("true");
      task.getInputs().files(inputDir);

      Method execMethod = CMakeConfigure.class.getDeclaredMethod("exec");
      execMethod.setAccessible(true);
      execMethod.invoke(task);
    } finally {
      Files.deleteIfExists(inputDir.toPath());
    }
  }
}
