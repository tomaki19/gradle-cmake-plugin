/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
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

class CMakeCustomTaskContainerExecTasksTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskContainer handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskContainer(project.getTasks());
  }

  // --- applyExecTasks(toolchain) ---

  @Test
  void applyExec_toolchain_registersWhenToolchainMatches() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_registersWithAllWildcard() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenSpecHasBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenSpecHasComponents() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "components",
        Set.of("myLib"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExecTasks(toolchain, buildConfig) ---

  @Test
  void applyExec_toolchainBuildConfig_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_registersWithAllWildcards() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "debug", t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "debug", t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_skipsWhenSpecHasComponents() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("myLib"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "release", t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExecTasks(toolchain, buildConfig, library) ---

  @Test
  void applyExec_library_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("myLib"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_registersWithLibrariesWildcard() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*library"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("anyLib", CMakeLinkVariant.SHARED),
        t -> {
        });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_skipsWhenLibraryNameNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherLib"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*library"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*library"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.SHARED),
        t -> {
        });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*library"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*library"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "debug", resolvedLibrary("myLib", CMakeLinkVariant.STATIC),
        t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExecTasks(toolchain, buildConfig, application) ---

  @Test
  void applyExec_application_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("myApp"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_registersWithApplicationsWildcard() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*application"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "debug", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_skipsWhenApplicationNameNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherApp"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*application"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*application"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*application"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "release", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*application"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "debug", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExecTasks(toolchain, buildConfig, test) ---

  @Test
  void applyExec_test_registersWhenMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("myTest"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_registersWithTestsWildcard() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"), "components", Set.of("*test"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "debug", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_skipsWhenTestNameNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"), "buildConfigs",
        Set.of("release"),
        "components", Set.of("otherTest"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_registersWhenHasNoToolchains() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "buildConfigs", Set.of("release"),
        "components", Set.of("*test"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_registersWhenHasNoBuildConfigs() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "components", Set.of("*test"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_skipsWhenToolchainNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("gcc"),
        "buildConfigs", Set.of("release"), "components", Set.of("*test"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("clang"), "release", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_skipsWhenBuildConfigNoMatch() {
    final Map<String, Object> spec = Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("release"), "components", Set.of("*test"));
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyExecTasks(resolvedToolchain("gcc"), "debug", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
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
