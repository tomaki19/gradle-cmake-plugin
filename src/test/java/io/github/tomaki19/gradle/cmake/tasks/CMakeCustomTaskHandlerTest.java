/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskSpec;
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
 * triggering abstract-class instantiation.  Tasks are verified by checking
 * project.getTasks().getNames() (realized on demand).
 */
class CMakeCustomTaskHandlerTest {

  private static final String TASK_NAME = "myCustomTask";

  private Project project;
  private CMakeCustomTaskHandler handler;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    handler = new CMakeCustomTaskHandler(project.getTasks());
  }

  // --- applyCustomExec(toolchain) ---

  @Test
  void applyCustomExec_toolchain_registersWhenToolchainMatches() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchain_registersWithAllWildcard() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("clang"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchain_skipsWhenToolchainNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("clang"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchain_skipsWhenSpecHasBuildConfigs() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchain_skipsWhenSpecHasComponents() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.components.add("myLib");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomExec(toolchain, buildConfig) ---

  @Test
  void applyCustomExec_toolchainBuildConfig_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release");

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchainBuildConfig_registersWithAllWildcards() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("clang"), "debug");

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchainBuildConfig_skipsWhenBuildConfigNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "debug");

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_toolchainBuildConfig_skipsWhenSpecHasComponents() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("myLib");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release");

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomExec(toolchain, buildConfig, library) ---

  @Test
  void applyCustomExec_library_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("myLib");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_library_registersWithLibrariesWildcard() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.LIBRARIES);
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedLibrary("anyLib", CMakeLinkVariant.SHARED));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_library_skipsWhenLibraryNameNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherLib");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomExec(toolchain, buildConfig, application) ---

  @Test
  void applyCustomExec_application_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("myApp");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_application_registersWithApplicationsWildcard() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.APPLICATIONS);
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("clang"), "debug", resolvedApplication("myApp"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_application_skipsWhenApplicationNameNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherApp");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedApplication("myApp"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomExec(toolchain, buildConfig, test) ---

  @Test
  void applyCustomExec_test_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("myTest");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedTest("myTest"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_test_registersWithTestsWildcard() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.TESTS);
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("clang"), "debug", resolvedTest("myTest"));

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomExec_test_skipsWhenTestNameNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherTest");
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedTest("myTest"));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomRuntimePackage (library) ---

  @Test
  void applyCustomRuntimePackage_library_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add(CMakeCustomTaskSpec.LIBRARIES);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.SHARED), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomRuntimePackage_library_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.LIBRARIES);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    final Action<AbstractArchiveTask> configureAction = t -> configureActionCalled.set(true);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), configureAction);

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomRuntimePackage_library_skipsWhenNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherLib");
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomRuntimePackage (application) ---

  @Test
  void applyCustomRuntimePackage_application_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.APPLICATIONS);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomRuntimePackage_application_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.APPLICATIONS);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> configureActionCalled.set(true));

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomRuntimePackage_application_skipsWhenNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherApp");
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomRuntimePackage (test) ---

  @Test
  void applyCustomRuntimePackage_test_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.TESTS);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomRuntimePackage_test_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.TESTS);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> configureActionCalled.set(true));

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomRuntimePackage_test_skipsWhenNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherTest");
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomDevelopPackage (library) ---

  @Test
  void applyCustomDevelopPackage_library_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add(CMakeCustomTaskSpec.STATIC);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomDevelopPackage_library_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.STATIC);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> configureActionCalled.set(true));

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomDevelopPackage_library_skipsWhenToolchainNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add(CMakeCustomTaskSpec.STATIC);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("clang"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomDevelopPackage (application) ---

  @Test
  void applyCustomDevelopPackage_application_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.APPLICATIONS);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "debug",
        resolvedApplication("myApp"), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomDevelopPackage_application_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.APPLICATIONS);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "debug",
        resolvedApplication("myApp"), t -> configureActionCalled.set(true));

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomDevelopPackage_application_skipsWhenNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherApp");
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "release",
        resolvedApplication("myApp"), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- applyCustomDevelopPackage (test) ---

  @Test
  void applyCustomDevelopPackage_test_registersWhenMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.TESTS);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "debug",
        resolvedTest("myTest"), t -> {});

    assertTrue(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void applyCustomDevelopPackage_test_appliesConfigureAction() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.TESTS);
    final AtomicBoolean configureActionCalled = new AtomicBoolean(false);
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "debug",
        resolvedTest("myTest"), t -> configureActionCalled.set(true));

    project.getTasks().named(TASK_NAME).get();
    assertTrue(configureActionCalled.get());
  }

  @Test
  void applyCustomDevelopPackage_test_skipsWhenNoMatch() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add("gcc");
    spec.buildConfigs.add("release");
    spec.components.add("otherTest");
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomDevelopPackage(resolvedToolchain("gcc"), "release",
        resolvedTest("myTest"), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- proto maps are independent: registering in one does not affect the others ---

  @Test
  void runtimePackageSpec_doesNotTriggerExec() {
    final CMakeCustomTaskSpec spec = allMatchingSpec();
    handler.registerCustomRuntimePackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void developPackageSpec_doesNotTriggerExec() {
    final CMakeCustomTaskSpec spec = allMatchingSpec();
    handler.registerCustomDevelopPackage(TASK_NAME, archiveType(), spec, t -> {});

    handler.applyCustomExec(resolvedToolchain("gcc"), "release", resolvedLibrary("myLib", CMakeLinkVariant.STATIC));

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  @Test
  void execSpec_doesNotTriggerRuntimePackage() {
    final CMakeCustomTaskSpec spec = allMatchingSpec();
    handler.registerCustomExec(TASK_NAME, CMakeCustomExec.class, spec, t -> {});

    handler.applyCustomRuntimePackage(resolvedToolchain("gcc"), "release",
        resolvedLibrary("myLib", CMakeLinkVariant.STATIC), t -> {});

    assertFalse(project.getTasks().getNames().contains(TASK_NAME));
  }

  // --- helpers ---

  private CMakeCustomTaskSpec allMatchingSpec() {
    final CMakeCustomTaskSpec spec = new CMakeCustomTaskSpec();
    spec.toolchains.add(CMakeCustomTaskSpec.ALL);
    spec.buildConfigs.add(CMakeCustomTaskSpec.ALL);
    spec.components.add(CMakeCustomTaskSpec.ALL);
    return spec;
  }

  @SuppressWarnings("unchecked")
  private Class<AbstractArchiveTask> archiveType() {
    return (Class<AbstractArchiveTask>) (Class<?>) Zip.class;
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
