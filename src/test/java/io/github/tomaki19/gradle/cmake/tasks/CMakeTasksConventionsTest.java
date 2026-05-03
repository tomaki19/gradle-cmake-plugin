/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeTasksConventionsTest {

  @Test
  void testAssembleListsTaskName() {
    assertEquals("assemble-cmake-lists",
        CMakeTasksConventions.assembleListsTaskName());
  }

  @Test
  void testAssembleFindTaskNameToolchain() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());

    assertEquals("assemble-mylib-shared-mytoolchain-debug-module",
        CMakeTasksConventions.assembleModuleTaskName(new CMakeResolvedLibrary(library, CMakeLinkVariant.SHARED, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testAssembleFindTaskNameProjectToolchain() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals(":MyProject:assemble-mylib-shared-mytoolchain-debug-module",
        CMakeTasksConventions.assembleModuleTaskName(
            new CMakeResolvedProjectDependency("MyLib", CMakeLinkVariant.SHARED, project, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testConfigureTaskNameProjectToolchainBuildConfig() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals(":MyProject:configure-mytoolchain-debug",
        CMakeTasksConventions.configureTaskName(
            new CMakeResolvedProjectDependency("MyLib", CMakeLinkVariant.SHARED, project, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testConfigureTaskNameToolchainBuildConfig() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals("configure-mytoolchain-debug",
        CMakeTasksConventions.configureTaskName(new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainLinkageBuildConfig() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeLibrary library = new MockCMakeLibrary("MyTarget", project.getObjects());

    assertEquals("build-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName(new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testBuildTaskNameTargetToolchainBuildConfig() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeApplication application = new MockCMakeApplication("MyTarget", project.getObjects());

    assertEquals("build-mytarget-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName(new CMakeResolvedApplication(application, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
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
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeLibrary library = new MockCMakeLibrary("MyTarget", project.getObjects());

    assertEquals("check-mytarget-static-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName(new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testCheckTaskNameTargetToolchainBuildConfig() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeApplication application = new MockCMakeApplication("MyTarget", project.getObjects());

    assertEquals("check-mytarget-mytoolchain-debug",
        CMakeTasksConventions.checkTaskName(new CMakeResolvedApplication(application, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
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
  void testCustomExecTaskName() {
    assertEquals("mytask-gcc-release",
        CMakeTasksConventions.customExecTaskName("myTask", "gcc", "Release"));
  }

  @Test
  void testBuildTaskNameProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals(":MyProject:build-mylib-shared-mytoolchain-debug",
        CMakeTasksConventions.buildTaskName(
            new CMakeResolvedProjectDependency("MyLib", CMakeLinkVariant.SHARED, project, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testArchiveDevelopTaskNameBinary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());

    assertEquals("develop-myapp-mytoolchain-debug",
        CMakeTasksConventions.archiveDevelopTaskName(
            new CMakeResolvedApplication(application, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

}
