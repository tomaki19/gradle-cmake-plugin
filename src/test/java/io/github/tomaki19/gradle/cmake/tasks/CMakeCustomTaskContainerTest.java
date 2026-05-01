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

/**
 * Uses a real Gradle project so that lazy task registration works without
 * triggering abstract-class instantiation. Tasks are verified by checking
 * project.getTasks().getNames() (realized on demand).
 */
class CMakeCustomTaskContainerTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskContainer handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskContainer(project.getTasks());
  }

  // --- applyExec(toolchain) ---

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

  // --- applyExec(toolchain, buildConfig) ---

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

  // --- applyExec(toolchain, buildConfig, library) ---

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

  // --- applyExec(toolchain, buildConfig, application) ---

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

  // --- applyExec(toolchain, buildConfig, test) ---

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

  // --- applyRuntimeArchive (library) ---

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

  // --- applyRuntimeArchive (application) ---

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

  // --- applyRuntimeArchive (test) ---

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

  // --- applyDevelopArchive (library) ---

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

  // --- proto maps are independent: registering in one does not affect the others
  // ---

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
    handler.registerExecTasks(spec, t -> {
    });

    handler.applyRuntimeArchiveTasks(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- helpers ---

  private Map<String, Object> allMatchingSpec() {
    return Map.of("name", TASK_NAME, "toolchains", Set.of("*"),
        "buildConfigs", Set.of("*"),
        "components", Set.of("*"));
  }

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
