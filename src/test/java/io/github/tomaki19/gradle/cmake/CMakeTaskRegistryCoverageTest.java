/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;

class CMakeTaskRegistryCoverageTest {

  private Project project;
  private CMakeResolvedToolchain toolchain;
  private CMakeTaskRegistry registry;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(BasePlugin.class);
    toolchain = new CMakeResolvedToolchain(new MockCMakeToolchain("test-toolchain", project.getObjects()));
    registry = new CMakeTaskRegistry();
  }

  @Test
  void testGroupConstants() {
    assertEquals("cmake build", CMakeTaskRegistry.GROUP_BUILD);
    assertEquals("cmake test", CMakeTaskRegistry.GROUP_CHECK);
    assertEquals("cmake install", CMakeTaskRegistry.GROUP_INSTALL);
  }

  @Test
  void testAssembleTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.assembleTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("assemble", taskProvider.getName());
  }

  @Test
  void testBuildTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.buildTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build", taskProvider.getName());
  }

  @Test
  void testCheckTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.checkTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check", taskProvider.getName());
  }

  @Test
  void testCleanTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.cleanTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("clean", taskProvider.getName());
  }

  @Test
  void testCleanListsTaskCreatesTask() {
    TaskProvider<CMakeClean> taskProvider = registry.cleanListsTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("clean-cmake-lists", taskProvider.getName());
  }

  @Test
  void testConfigureTaskCreatesTask() {
    TaskProvider<CMakeConfigure> taskProvider = registry.configureTask(project.getTasks(), toolchain, "Debug");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("configure-test-toolchain-debug", taskProvider.getName());
  }

  @Test
  void testBuildAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.buildAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testBuildAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.buildAllBuildConfigTask(project.getTasks(), toolchain, "Release");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build-all-test-toolchain-release", taskProvider.getName());
  }

  @Test
  void testCheckAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.checkAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testCheckAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.checkAllBuildConfigTask(project.getTasks(), toolchain, "Debug");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check-all-test-toolchain-debug", taskProvider.getName());
  }

  @Test
  void testInstallAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.installAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("install-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testInstallAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.installAllBuildConfigTask(project.getTasks(), toolchain, "Release");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("install-all-test-toolchain-release", taskProvider.getName());
  }
}
