/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

class CMakeFileConventionsTest {

  @Test
  void testConstants() {
    assertEquals("cmake/config", CMakeFileConventions.CMAKE_CONFIG_PATH);
    assertEquals("cmake/install", CMakeFileConventions.CMAKE_INSTALL_PATH);
  }

  @Test
  void testModuleTargetWithLinkage() {
    assertEquals("MyProject-mytarget-static-mytoolchain-debug-module",
        CMakeFileConventions.moduleTarget("MyProject", "MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTargetWithLinkage() {
    assertEquals("mytarget-static-mytoolchain-debug",
        CMakeFileConventions.buildTarget("MyTarget", CMakeLinkType.STATIC, "MyToolchain", "Debug"));
  }

  @Test
  void testBuildTargetWithoutLinkage() {
    assertEquals("mytarget-mytoolchain-debug",
        CMakeFileConventions.buildTarget("MyTarget", "MyToolchain", "Debug"));
  }
}
