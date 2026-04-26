/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_registersWithAllWildcard() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("clang"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenToolchainNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("clang"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenSpecHasBuildConfigs() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchain_skipsWhenSpecHasComponents() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "components",
        Arrays.asList("myLib"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExec(toolchain, buildConfig) ---

  @Test
  void applyExec_toolchainBuildConfig_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_registersWithAllWildcards() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("clang"), "debug", t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_skipsWhenBuildConfigNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "debug", t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_toolchainBuildConfig_skipsWhenSpecHasComponents() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("myLib"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExec(toolchain, buildConfig, library) ---

  @Test
  void applyExec_library_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("myLib"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_registersWithLibrariesWildcard() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*library"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedLibrary("anyLib", CMakeLinkVariant.SHARED),
        t -> {
        });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_library_skipsWhenLibraryNameNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherLib"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExec(toolchain, buildConfig, application) ---

  @Test
  void applyExec_application_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("myApp"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_registersWithApplicationsWildcard() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*application"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("clang"), "debug", resolvedApplication("myApp"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_application_skipsWhenApplicationNameNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherApp"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyExec(toolchain, buildConfig, test) ---

  @Test
  void applyExec_test_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("myTest"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_registersWithTestsWildcard() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*test"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("clang"), "debug", resolvedTest("myTest"), t -> {
    });

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyExec_test_skipsWhenTestNameNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherTest"));
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedTest("myTest"), t -> {
    });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyRuntimePackage (library) ---

  @Test
  void applyRuntimePackage_library_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("*library"));
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.SHARED), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-runtime-mylib-shared-gcc-release"));
  }

  @Test
  void applyRuntimePackage_library_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*library"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    final Action<AbstractArchiveTask> configureAction = t -> configureActionCalled.set(true);
    handler.registerRuntimePackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), configureAction);

    project.getTasks().named("pkg-runtime-mylib-static-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimePackage_library_skipsWhenNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherLib"));
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-runtime-mylib-static-gcc-release"));
  }

  // --- applyRuntimePackage (application) ---

  @Test
  void applyRuntimePackage_application_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*application"));
    handler.registerRuntimePackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-runtime-myapp-gcc-release"));
  }

  @Test
  void applyRuntimePackage_application_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*application"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> configureActionCalled.set(true));

    project.getTasks().named("pkg-runtime-myapp-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimePackage_application_skipsWhenNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherApp"));
    handler.registerRuntimePackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-runtime-myapp-gcc-release"));
  }

  // --- applyRuntimePackage (test) ---

  @Test
  void applyRuntimePackage_test_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*test"));
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-runtime-mytest-gcc-release"));
  }

  @Test
  void applyRuntimePackage_test_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*test"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerRuntimePackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> configureActionCalled.set(true));

    project.getTasks().named("pkg-runtime-mytest-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyRuntimePackage_test_skipsWhenNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherTest"));
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-runtime-mytest-gcc-release"));
  }

  // --- applyDevelopPackage (library) ---

  @Test
  void applyDevelopPackage_library_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("*static"));
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-develop-mylib-static-gcc-release"));
  }

  @Test
  void applyDevelopPackage_library_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*static"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerDevelopPackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> configureActionCalled.set(true));

    project.getTasks().named("pkg-develop-mylib-static-gcc-release").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyDevelopPackage_library_skipsWhenToolchainNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("*static"));
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("clang"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-develop-mylib-static-clang-release"));
  }

  // --- applyDevelopPackage (application) ---

  @Test
  void applyDevelopPackage_application_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*application"));
    handler.registerDevelopPackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "debug",
        resolvedApplication("myApp"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-develop-myapp-gcc-debug"));
  }

  @Test
  void applyDevelopPackage_application_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*application"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "debug",
        resolvedApplication("myApp"), t -> configureActionCalled.set(true));

    project.getTasks().named("pkg-develop-myapp-gcc-debug").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyDevelopPackage_application_skipsWhenNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherApp"));
    handler.registerDevelopPackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-develop-myapp-gcc-release"));
  }

  // --- applyDevelopPackage (test) ---

  @Test
  void applyDevelopPackage_test_registersWhenMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*test"));
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "debug",
        resolvedTest("myTest"), t -> {
        });

    assertTrue(project.getTasks().getNames().contains("pkg-develop-mytest-gcc-debug"));
  }

  @Test
  void applyDevelopPackage_test_appliesConfigureAction() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"), "components", Arrays.asList("*test"));
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerDevelopPackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "debug",
        resolvedTest("myTest"), t -> configureActionCalled.set(true));

    project.getTasks().named("pkg-develop-mytest-gcc-debug").get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyDevelopPackage_test_skipsWhenNoMatch() {
    final Map<String, List<Object>> spec = Map.of("toolchains", Arrays.asList("gcc"), "buildConfigs",
        Arrays.asList("release"),
        "components", Arrays.asList("otherTest"));
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyDevelopPackageTask(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {
        });

    assertFalse(project.getTasks().getNames().contains("pkg-develop-mytest-gcc-release"));
  }

  // --- proto maps are independent: registering in one does not affect the others
  // ---

  @Test
  void runtimePackageSpec_doesNotTriggerExec() {
    final Map<String, List<Object>> spec = allMatchingSpec();
    handler.registerRuntimePackageTask(spec, CMakeCustomTar.class, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("pkg-runtime-mylib-static-gcc-release"));
  }

  @Test
  void developPackageSpec_doesNotTriggerExec() {
    final Map<String, List<Object>> spec = allMatchingSpec();
    handler.registerDevelopPackageTask(spec, CMakeCustomZip.class, t -> {
    });

    handler.applyExecTask(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
    });

    assertFalse(project.getTasks().getNames().contains("pkg-develop-mylib-static-gcc-release"));
  }

  @Test
  void execSpec_doesNotTriggerRuntimePackage() {
    final Map<String, List<Object>> spec = allMatchingSpec();
    handler.registerExecTask(spec, TASK_NAME, t -> {
    });

    handler.applyRuntimePackageTask(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {
        });

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- helpers ---

  private Map<String, List<Object>> allMatchingSpec() {
    return Map.of("toolchains", Arrays.asList("*"),
        "buildConfigs", Arrays.asList("*"),
        "components", Arrays.asList("*"));
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
