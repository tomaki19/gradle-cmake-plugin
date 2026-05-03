/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeCustomTaskContainer;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

/**
 * Verifies that specs registered under one task type do not trigger registration
 * under a different task type (proto maps are independent).
 */
class CMakeCustomTaskContainerIsolationTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskContainer handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskContainer(project.getTasks());
  }

  @Test
  void runtimeArchiveSpec_doesNotTriggerExec() {
    final Map<String, Object> spec = allMatchingSpec();
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mylib-static-gcc-release"));
  }

  @Test
  void developArchiveSpec_doesNotTriggerExec() {
    final Map<String, Object> spec = allMatchingSpec();
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-release"));
  }

  @Test
  void execSpec_doesNotTriggerRuntimeArchive() {
    final Map<String, Object> spec = allMatchingSpec();
    handler.registerExecTasks(spec, TASK_NAME, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- helpers ---

  private Map<String, Object> allMatchingSpec() {
    return Map.of("toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"),
        "components", Set.of("*"));
  }

  private CMakeResolvedToolchain resolvedToolchain(final String name) {
    return new CMakeResolvedToolchain(new MockCMakeToolchain(name, project.getObjects()));
  }

  private CMakeResolvedLibrary resolvedLibrary(final String name, final CMakeLinkVariant variant) {
    return new CMakeResolvedLibrary(new MockCMakeLibrary(name, project.getObjects()), variant, false);
  }
}
