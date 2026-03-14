/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;

class CMakeTasksConventionsTest {

  @Test
  void testAssembleListsTaskName() {
    assertEquals("assemble-cmake-lists",
        CMakeTasksConventions.assembleListsTaskName());
  }

  @Test
  void testAssembleFindTaskNameToolchain() {
    assertEquals("assemble-mylib-shared-mytoolchain-debug-module",
        CMakeTasksConventions.assembleModuleTaskName("MyLib", CMakeLinkVariant.SHARED, "MyToolchain", "Debug"));
  }

  @Test
  void testAssembleFindTaskNameProjectToolchain() {
    assertEquals(":MyProject:assemble-mylib-shared-mytoolchain-debug-module",
        CMakeTasksConventions.assembleModuleTaskName("MyProject", "MyLib", CMakeLinkVariant.SHARED, "MyToolchain",
            "Debug"));
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
        CMakeTasksConventions.buildTaskName("MyProject", "MyTarget", CMakeLinkVariant.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("build-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName("MyTarget", CMakeLinkVariant.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainBuildConfig() {
    assertEquals("build-mytarget-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName("MyTarget", "MyToolchain", "Debug"));
  }

  @Test
  void testBuildAllToolchainTaskName() {
    assertEquals("build-all-mytoolchain", CMakeTasksConventions.buildAllToolchainTaskName("MyToolchain"));
  }

  @Test
  void testBuildAllBuildConfigTaskName() {
    assertEquals("build-all-mytoolchain-debug",
        CMakeTasksConventions.buildAllBuildConfigTaskName("MyToolchain", "Debug"));
  }

  @Test
  void testCheckTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("check-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName("MyTarget", CMakeLinkVariant.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testCheckTaskNameTargetToolchainBuildConfig() {
    assertEquals("check-mytarget-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName("MyTarget", "MyToolchain", "Debug"));
  }

  @Test
  void testCheckAllToolchainTaskName() {
    assertEquals("check-all-mytoolchain", CMakeTasksConventions.checkAllToolchainTaskName("MyToolchain"));
  }

  @Test
  void testCheckBuildConfigTaskName() {
    assertEquals("check-all-mytoolchain-debug",
        CMakeTasksConventions.checkAllBuildConfigTaskName("MyToolchain", "Debug"));
  }

  @Test
  void testPackageTaskNameTargetToolchainLinkageBuildConfig() {
    assertEquals("install-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.installTaskName("MyTarget", CMakeLinkVariant.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testPackageTaskNameTargetToolchainBuildConfig() {
    assertEquals("install-mytarget-mytoolchain-debug",
        CMakeTasksConventions.installTaskName("MyTarget", "MyToolchain", "Debug"));
  }
}
