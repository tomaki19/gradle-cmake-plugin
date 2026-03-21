/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;

class CMakePluginFullCoverageTest {

  private Project project;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().build();
  }

  @Test
  void testPluginAppliedAndExtensionCreated() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    assertNotNull(extension);
  }

  @Test
  void testLifecycleTasksExistAfterPluginApplied() {
    project.getPluginManager().apply(CMakePlugin.class);

    assertNotNull(project.getTasks().findByName("clean"));
    assertNotNull(project.getTasks().findByName("assemble"));
    assertNotNull(project.getTasks().findByName("build"));
    assertNotNull(project.getTasks().findByName("check"));
  }

  @Test
  void testExtensionContainersNotNull() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    assertNotNull(extension.getPackages());
    assertNotNull(extension.getToolchains());
    assertNotNull(extension.getLibraries());
    assertNotNull(extension.getApplications());
    assertNotNull(extension.getTests());
  }

  @Test
  void testSoftwareComponentRegistered() {
    project.getPluginManager().apply(CMakePlugin.class);

    assertTrue(project.getComponents().stream().anyMatch(c -> c.getName().equals("cmake")));
  }

  @Test
  void testMultipleProjectsCanApplyPlugin() {
    Project project1 = ProjectBuilder.builder().build();
    Project project2 = ProjectBuilder.builder().build();

    project1.getPluginManager().apply(CMakePlugin.class);
    project2.getPluginManager().apply(CMakePlugin.class);

    assertNotNull(project1.getExtensions().getByType(CMakeExtension.class));
    assertNotNull(project2.getExtensions().getByType(CMakeExtension.class));
  }

  @Test
  void testExtensionRegisterMethodAcceptsCustomTask() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    // With no toolchains configured the action is never invoked, but the call must not throw
    extension.register("myTask", proto -> {});
    assertTrue(true);
  }
}
