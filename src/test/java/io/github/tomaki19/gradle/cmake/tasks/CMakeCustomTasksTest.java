/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeTaskContainer;

class CMakeCustomTasksTest {

  private Project project;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().withName("myProject").build();
  }

  // --- CMakeCustomTar ---

  @Test
  void cmakeCustomTar_setsGroupOnConstruction() {
    final CMakeCustomTar task = project.getTasks().register("myTar", CMakeCustomTar.class).get();
    assertEquals(CMakeTaskContainer.GROUP_PACKAGE, task.getGroup());
  }

  @Test
  void cmakeCustomTar_setsArchiveBaseNameToProjectName() {
    final CMakeCustomTar task = project.getTasks().register("myTar", CMakeCustomTar.class).get();
    assertEquals("myProject", task.getArchiveBaseName().get());
  }

  // --- CMakeCustomExec ---

  @Test
  void cmakeCustomExec_isInstantiatedWithToolchainAndBuildConfig() {
    final CMakeCustomExec task = project.getTasks()
        .register("myExec", CMakeCustomExec.class, "gcc", "release", Optional.empty()).get();
    assertNotNull(task);
  }

  @Test
  void cmakeCustomExec_setsCompileCommandsPath() {
    final CMakeCustomExec task = project.getTasks()
        .register("myExec", CMakeCustomExec.class, "clang", "debug", Optional.empty()).get();
    assertNotNull(task.compileCommands);
    assertTrue(task.compileCommands.endsWith("compile_commands.json"));
  }
}
