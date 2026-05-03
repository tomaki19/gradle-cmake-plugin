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

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeCustomTaskContainer;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeLibrary;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeCustomTaskContainerRuntimeArchiveTasksTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskContainer handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskContainer(project.getTasks());
  }

  // --- applyRuntimeArchiveTasks (library) ---

  @Test
  void applyRuntimeArchive_library_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.SHARED), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mylib-shared-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_library_appliesConfigureAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*library"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    final Action<AbstractArchiveTask> configureAction = t -> configureActionCalled.set(true);
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), configureAction);

    project.getTasks().named("zip-runtime-mylib-static-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimeArchive_library_skipsWhenNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherLib"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mylib-static-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_library_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mylib-static-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_library_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.SHARED), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mylib-shared-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_library_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("clang"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mylib-static-clang-release"));
  }

  @Test
  void applyRuntimeArchive_library_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "debug",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mylib-static-gcc-debug"));
  }

  // --- applyRuntimeArchiveTasks (application) ---

  @Test
  void applyRuntimeArchive_application_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*application"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-myapp-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_application_appliesConfigureAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*application"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> configureActionCalled.set(true));

    project.getTasks().named("zip-runtime-myapp-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimeArchive_application_skipsWhenNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherApp"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-myapp-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_application_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*application"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-myapp-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_application_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*application"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "debug", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-myapp-gcc-debug"));
  }

  @Test
  void applyRuntimeArchive_application_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*application"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("clang"), "release", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-myapp-clang-release"));
  }

  @Test
  void applyRuntimeArchive_application_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*application"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "debug", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-myapp-gcc-debug"));
  }

  // --- applyRuntimeArchiveTasks (test) ---

  @Test
  void applyRuntimeArchive_test_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*test"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mytest-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_test_appliesConfigureAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*test"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> configureActionCalled.set(true));

    project.getTasks().named("zip-runtime-mytest-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimeArchive_test_skipsWhenNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherTest"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mytest-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_test_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*test"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mytest-gcc-release"));
  }

  @Test
  void applyRuntimeArchive_test_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*test"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "debug", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains("zip-runtime-mytest-gcc-debug"));
  }

  @Test
  void applyRuntimeArchive_test_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*test"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("clang"), "release", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mytest-clang-release"));
  }

  @Test
  void applyRuntimeArchive_test_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*test"));
    handler.registerRuntimeArchiveTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "debug", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("zip-runtime-mytest-gcc-debug"));
  }

  // --- no-arg overload tests ---

  @Test
  void registerRuntimeArchiveTasks_noArgOverload_usesDefaultAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*library"));
    handler.registerRuntimeArchiveTasks(spec);

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.SHARED), t -> {
        });

    project.getTasks().named("zip-runtime-mylib-shared-gcc-release").get();
    assertTrue(project.getTasks().getNames().contains("zip-runtime-mylib-shared-gcc-release"));
  }

  @Test
  void registerDevelopArchiveTasks_noArgOverload_usesDefaultAction() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*static"));
    handler.registerDevelopArchiveTasks(spec);

    handler.applyDevelopArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

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

  private CMakeResolvedApplication resolvedApplication(final String name) {
    return new CMakeResolvedApplication(new MockCMakeApplication(name, project.getObjects()), false);
  }

  private CMakeResolvedTest resolvedTest(final String name) {
    return new CMakeResolvedTest(new MockCMakeTest(name, project.getObjects()), false);
  }
}
