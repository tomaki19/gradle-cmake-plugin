/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

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
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeFileConventionsTest {

  @Test
  void testConstants() {
    assertEquals("cmake/config", CMakeFileConventions.CMAKE_CONFIG_PATH);
    assertEquals("cmake/install", CMakeFileConventions.CMAKE_INSTALL_PATH);
  }

  @Test
  void testModuleTargetWithLinkage() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeLibrary library = new MockCMakeLibrary("MyTarget", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals("myproject-mytarget-static-mytoolchain-debug-module",
        CMakeFileConventions.moduleTarget(project, new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testBuildTargetWithLinkage() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyTarget", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    assertEquals("mytarget-static-mytoolchain-debug",
        CMakeFileConventions.buildTarget(new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }

  @Test
  void testBuildTargetWithoutLinkage() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("MyTarget", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    assertEquals("mytarget-mytoolchain-debug",
        CMakeFileConventions.buildTarget(new CMakeResolvedExecutable(application, false),
            new CMakeResolvedToolchain(toolchain), "Debug"));
  }
}
