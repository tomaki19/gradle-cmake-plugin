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
  void testCreateModulesNameForExecutable() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedApplication executable = new CMakeResolvedApplication(application, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesName(executable, resolvedToolchain, "Debug");
    assertEquals("myapp-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateModulesNameForStaticLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesName(resolvedLibrary, resolvedToolchain,
        "Debug");
    assertEquals("mylib-static-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateModulesNameForSharedLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.SHARED, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesName(resolvedLibrary, resolvedToolchain,
        "Release");
    assertEquals("mylib-shared-mytoolchain-release-modules", result);
  }

  @Test
  void testCreateModulesNameForInterfaceLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.INTERFACE, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesName(resolvedLibrary, resolvedToolchain,
        "Release");
    assertEquals("mylib-interface-mytoolchain-release-modules", result);
  }

  @Test
  void testCreateModulesNameForProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createModulesName(dependency, resolvedToolchain,
        "Debug");
    assertEquals("mylib-shared-mytoolchain-debug-modules", result);
  }

  @Test
  void testCreateRuntimeNameForExecutable() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeApplication application = new MockCMakeApplication("MyApp", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedApplication executable = new CMakeResolvedApplication(application, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createRuntimeName(executable, resolvedToolchain, "Debug");
    assertEquals("myapp-mytoolchain-debug-runtime", result);
  }

  @Test
  void testCreateRuntimeNameForStaticLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.STATIC, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createRuntimeName(resolvedLibrary, resolvedToolchain,
        "Debug");
    assertEquals("mylib-static-mytoolchain-debug-runtime", result);
  }

  @Test
  void testCreateRuntimeNameForSharedLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.SHARED, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createRuntimeName(resolvedLibrary, resolvedToolchain,
        "Release");
    assertEquals("mylib-shared-mytoolchain-release-runtime", result);
  }

  @Test
  void testCreateRuntimeNameForInterfaceLibrary() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeLibrary library = new MockCMakeLibrary("MyLib", project.getObjects());
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library, CMakeLinkVariant.INTERFACE, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createRuntimeName(resolvedLibrary, resolvedToolchain,
        "Release");
    assertEquals("mylib-interface-mytoolchain-release-runtime", result);
  }

  @Test
  void testCreateRuntimeNameForProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createRuntimeName(dependency, resolvedToolchain,
        "Debug");
    assertEquals("mylib-shared-mytoolchain-debug-runtime", result);
  }

  @Test
  void testCreateDevelopNameForProjectDependency() {
    final Project project = ProjectBuilder.builder().withName("MyProject").build();
    final CMakeToolchain toolchain = new MockCMakeToolchain("MyToolchain", project.getObjects());

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("MyLib",
        CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);

    final String result = CMakeConfigurationConventions.createDevelopName(dependency, resolvedToolchain, "Debug");
    assertEquals("mylib-shared-mytoolchain-debug-develop", result);
  }

}
