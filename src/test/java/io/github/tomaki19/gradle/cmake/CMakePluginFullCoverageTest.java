/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

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
  void testExtensionRegisterMethodWithNoToolchains() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    // With no toolchains the action is never invoked, but the call must not throw
    extension.register("myTask", proto -> {});
    assertTrue(true);
  }

  @Test
  void testExtensionRegisterWithToolchainNameFilter() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));

    // register(name, toolchainList, action) overload
    extension.register("myTask", List.of("gcc"), proto -> {});
    assertTrue(true);
  }

  @Test
  void testExtensionRegisterWithToolchainAndBuildConfigFilter() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));

    // register(name, toolchainList, buildConfigList, action) overload
    extension.register("myTask", List.of("gcc"), List.of("Debug"), proto -> {});
    assertTrue(true);
  }

  @Test
  void testPluginEvaluateWithEmptyExtension() {
    project.getPluginManager().apply(CMakePlugin.class);

    ((ProjectInternal) project).evaluate();

    assertNotNull(project.getTasks().findByName("clean-cmake-lists"));
    assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
  }

  @Test
  void testPluginEvaluateWithStaticLibrary() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    extension.getLibraries().register("mylib", lib -> {
      lib.getHeaders().srcDir(project.getProjectDir());
      lib.getSources().srcDir(project.getProjectDir());
      lib.buildVariants(CMakeBuildVariant.STATIC);
    });

    ((ProjectInternal) project).evaluate();

    assertNotNull(project.getTasks().findByName("clean-cmake-lists"));
    assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
    assertNotNull(project.getTasks().findByName("configure-gcc-debug"));
    assertNotNull(project.getTasks().findByName("build-all-gcc"));
    assertNotNull(project.getTasks().findByName("build-mylib-static-gcc-debug"));
    assertNotNull(project.getTasks().findByName("install-mylib-static-gcc-debug"));
  }

  @Test
  void testPluginEvaluateWithSharedLibrary() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("clang", tc -> tc.buildConfigs("Release"));
    extension.getLibraries().register("sharedlib", lib -> {
      lib.getHeaders().srcDir(project.getProjectDir());
      lib.getSources().srcDir(project.getProjectDir());
      lib.buildVariants(CMakeBuildVariant.SHARED);
    });

    ((ProjectInternal) project).evaluate();

    assertNotNull(project.getTasks().findByName("build-sharedlib-shared-clang-release"));
    assertNotNull(project.getTasks().findByName("install-sharedlib-shared-clang-release"));
  }

  @Test
  void testPluginEvaluateWithApplication() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    extension.getApplications().register("myapp", app -> {
      app.getHeaders().srcDir(project.getProjectDir());
      app.getSources().srcDir(project.getProjectDir());
    });

    ((ProjectInternal) project).evaluate();

    assertNotNull(project.getTasks().findByName("build-myapp-gcc-debug"));
    assertNotNull(project.getTasks().findByName("install-myapp-gcc-debug"));
  }

  @Test
  void testPluginEvaluateWithTest() {
    project.getPluginManager().apply(CMakePlugin.class);

    CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
    extension.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    extension.getTests().register("mytest", t -> {
      t.getHeaders().srcDir(project.getProjectDir());
      t.getSources().srcDir(project.getProjectDir());
    });

    ((ProjectInternal) project).evaluate();

    assertNotNull(project.getTasks().findByName("build-mytest-gcc-debug"));
    assertNotNull(project.getTasks().findByName("check-mytest-gcc-debug"));
    assertNotNull(project.getTasks().findByName("check-all-gcc"));
  }

  @Test
  void testCMakeVisibilityTypeLowerCase() {
    assertEquals("public", CMakeVisibilityType.PUBLIC.toLowerCase());
    assertEquals("private", CMakeVisibilityType.PRIVATE.toLowerCase());
  }

  @Test
  void testCMakeBuildVariantLowerCase() {
    assertEquals("static", CMakeBuildVariant.STATIC.toLowerCase());
    assertEquals("shared", CMakeBuildVariant.SHARED.toLowerCase());
    assertEquals("module", CMakeBuildVariant.MODULE.toLowerCase());
  }

  @Test
  void testResolvedToolchainGetters() {
    CMakeResolvedToolchain tc = new CMakeResolvedToolchain(
        new MockCMakeToolchain("my-toolchain", ProjectBuilder.builder().build().getObjects()));

    assertNotNull(tc.getOperatingSystem());
    assertNotNull(tc.getGenerator());
    assertNotNull(tc.getEnvironment());
    assertNotNull(tc.getEnvironmentFile());
    assertNotNull(tc.getToolchainFile());
  }
}
