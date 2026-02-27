/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

class CMakeTasksConventionsTest {

  @Test
  void testAssembleListsTaskName() {
    assertEquals("assemble-cmake-lists",
        CMakeTasksConventions.assembleListsTaskName());
  }

  @Test
  void testAssembleFindTaskNameToolchain() {
    assertEquals("assemble-cmake-modules-mytoolchain-debug",
        CMakeTasksConventions.assembleModulesTaskName("MyToolchain", "Debug"));
  }

  @Test
  void testAssembleFindTaskNameProjectToolchain() {
    assertEquals(":MyProject:assemble-cmake-modules-mytoolchain-debug",
        CMakeTasksConventions.assembleModulesTaskName("MyProject", "MyToolchain", "Debug"));
  }

  @Test
  void testCustomExecTaskName() {
    assertEquals("mytask-mytoolchain-debug",
        CMakeTasksConventions.customExecTaskName("mytask", "MyToolchain", "Debug"));
  }

  @Test
  void testConfigureTaskNameProjectToolchainBuildConfig() {
    assertEquals(":MyProject:configure-mytoolchain-debug",
        CMakeTasksConventions.configureTaskName("MyProject", "MyToolchain", "Debug"));
  }

  @Test
  void testConfigureTaskNameToolchainBuildConfig() {
    assertEquals("configure-mytoolchain-debug",
        CMakeTasksConventions.configureTaskName("MyToolchain", "Debug"));
  }

  @Test
  void testBuildTaskNameProjectTargetToolchainLinkageBuildConfig() {
    assertEquals(":MyProject:build-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName("MyProject", "MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("build-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName("MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainBuildConfig() {
    assertEquals("build-mytarget-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName("MyTarget", "MyToolchain", "Debug"));
  }

  @Test
  void testBuildAllTaskName() {
    assertEquals("build-all-mytoolchain", CMakeTasksConventions.buildAllTaskName("MyToolchain"));
  }

  @Test
  void testCheckTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("check-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName("MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testCheckTaskNameTargetToolchainBuildConfig() {
    assertEquals("check-mytarget-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName("MyTarget", "MyToolchain", "Debug"));
  }

  @Test
  void testCheckAllTaskName() {
    assertEquals("check-all-mytoolchain", CMakeTasksConventions.checkAllTaskName("MyToolchain"));
  }

  @Test
  void testPackageTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("package-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.packageTaskName("MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testPackageTaskNameTargetToolchainBuildConfig() {
    assertEquals("package-mytarget-mytoolchain-debug",
        CMakeTasksConventions.packageTaskName("MyTarget", "MyToolchain", "Debug"));
  }
}
