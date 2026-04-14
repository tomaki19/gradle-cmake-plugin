/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
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
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedProjectDependency;
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

  @Test
  void testTargetConfigDirectoryWithToolchain() {
    final Project project = ProjectBuilder.builder().build();
    final Directory buildDir = project.getLayout().getBuildDirectory().get();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final Directory result = CMakeFileConventions.targetConfigDirectory(buildDir, toolchain, "Debug");
    assertNotNull(result);
    assertTrue(result.getAsFile().getPath().contains("MyToolchain"));
    assertTrue(result.getAsFile().getPath().contains("Debug"));
  }

  @Test
  void testTargetBinaryDirectoryWithExecutable() {
    final Project project = ProjectBuilder.builder().build();
    final Directory buildDir = project.getLayout().getBuildDirectory().get();
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeResolvedExecutable resolvedExec = new CMakeResolvedExecutable(application, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    final Directory result = CMakeFileConventions.targetBinaryDirectory(buildDir, resolvedExec, resolvedToolchain,
        "Debug");
    assertNotNull(result);
    assertTrue(result.getAsFile().getPath().contains("myapp-mytoolchain-debug"));
  }

  @Test
  void testTargetBinaryDirectoryWithProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("DepProject").build();
    final Directory buildDir = project.getLayout().getBuildDirectory().get();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.STATIC, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    final Directory result = CMakeFileConventions.targetBinaryDirectory(buildDir, dependency, resolvedToolchain,
        "Debug");
    assertNotNull(result);
    assertTrue(result.getAsFile().getPath().contains("mylib-static-mytoolchain-debug"));
  }

  @Test
  void testModuleTargetWithProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("DepProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertEquals("depproject-mylib-shared-mytoolchain-debug-module",
        CMakeFileConventions.moduleTarget(dependency, resolvedToolchain, "Debug"));
  }

  @Test
  void testBuildTargetWithProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("DepProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.STATIC, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
    assertEquals("mylib-static-mytoolchain-debug",
        CMakeFileConventions.buildTarget(dependency, resolvedToolchain, "Debug"));
  }
}
