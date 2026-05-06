/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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

class CMakeCustomTaskContainerDevelopArchiveTasksTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskContainer handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskContainer(project.getTasks());
  }

  // --- applyDevelopArchiveTasks (library) ---

  @Test
  void applyDevelopArchive_library_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-release"));
  }

  @Test
  void applyDevelopArchive_library_appliesConfigureAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*static"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> configureActionCalled.set(true));

    project.getTasks().named("zip-develop-mylib-static-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyDevelopArchive_library_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("clang"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-develop-mylib-static-clang-release"));
  }

  @Test
  void applyDevelopArchive_library_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-release"));
  }

  @Test
  void applyDevelopArchive_library_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "debug",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-debug"));
  }

  @Test
  void applyDevelopArchive_library_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec, t -> {
    });

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "debug",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-debug"));
  }

  @Test
  void registerDevelopArchive_noAction_registersTaskWithDefaultEmptyAction() {
    final Map<String, Object> spec = Map.of("toolchains", Set.of("*"), "buildConfigs", Set.of("*"),
        "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec); // no-action overload: covers registerDevelopArchiveTasks(Map)

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    // Realizing the task triggers the default empty-action lambda
    project.getTasks().named("zip-develop-mylib-static-gcc-release").get();
    assertTrue(project.getTasks().getNames().contains("zip-develop-mylib-static-gcc-release"));
  }

  // --- helpers ---

  private CMakeResolvedToolchain resolvedToolchain(final String name) {
    return new CMakeResolvedToolchain(new MockCMakeToolchain(name, project.getObjects()));
  }

  private CMakeResolvedLibrary resolvedLibrary(final String name, final CMakeLinkVariant variant) {
    return new CMakeResolvedLibrary(new MockCMakeLibrary(name, project.getObjects()), variant, false);
  }
}
