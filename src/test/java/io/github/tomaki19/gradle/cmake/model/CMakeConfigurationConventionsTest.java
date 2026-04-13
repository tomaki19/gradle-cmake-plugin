/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

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

class CMakeConfigurationConventionsTest {

  @Test
  void testCreateModulesDirectoriesNameForExecutable() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedExecutable executable = new CMakeResolvedExecutable(application, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesDirectoriesName(executable, resolvedToolchain, "Debug");
    assertEquals("myapp-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateModulesDirectoriesNameForStaticLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesDirectoriesName(resolvedLibrary, resolvedToolchain, "Debug");
    assertEquals("mylib-static-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateModulesDirectoriesNameForSharedLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.SHARED, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesDirectoriesName(resolvedLibrary, resolvedToolchain, "Release");
    assertEquals("mylib-shared-mytoolchain-release-modules", result);
  }

  @Test
  void testCreateModulesDirectoriesNameForInterfaceLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.INTERFACE, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesDirectoriesName(resolvedLibrary, resolvedToolchain, "Release");
    assertEquals("mylib-interface-mytoolchain-release-modules", result);
  }

  @Test
  void testCreateModulesDirectoriesNameForProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib", CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesDirectoriesName(dependency, resolvedToolchain, "Debug");
    assertEquals("mylib-shared-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateOutputDirectoriesNameForExecutable() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedExecutable executable = new CMakeResolvedExecutable(application, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createOutputDirectoriesName(executable, resolvedToolchain, "Debug");
    assertEquals("myapp-mytoolchain-debug-outputs", result);
  }

  @Test
  void testCreateOutputDirectoriesNameForStaticLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createOutputDirectoriesName(resolvedLibrary, resolvedToolchain, "Debug");
    assertEquals("mylib-static-mytoolchain-debug-outputs", result);
  }

  @Test
  void testCreateOutputDirectoriesNameForSharedLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.SHARED, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createOutputDirectoriesName(resolvedLibrary, resolvedToolchain, "Release");
    assertEquals("mylib-shared-mytoolchain-release-outputs", result);
  }

  @Test
  void testCreateOutputDirectoriesNameForProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib", CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createOutputDirectoriesName(dependency, resolvedToolchain, "Debug");
    assertEquals("mylib-shared-mytoolchain-debug-outputs", result);
  }

}
