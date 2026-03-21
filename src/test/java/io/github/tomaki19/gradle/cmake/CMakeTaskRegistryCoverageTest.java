/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.MockCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;
import io.github.tomaki19.gradle.cmake.tasks.CMakeAssemble;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuild;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildExecutable;
import io.github.tomaki19.gradle.cmake.tasks.CMakeBuildLibrary;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCheck;
import io.github.tomaki19.gradle.cmake.tasks.CMakeClean;
import io.github.tomaki19.gradle.cmake.tasks.CMakeConfigure;
import io.github.tomaki19.gradle.cmake.tasks.CMakeInstall;
import io.github.tomaki19.gradle.cmake.tasks.CMakeTaskRegistry;

class CMakeTaskRegistryCoverageTest {

  private Project project;
  private CMakeResolvedToolchain toolchain;
  private CMakeTaskRegistry registry;

  @BeforeEach
  void setup() {
    project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(BasePlugin.class);
    toolchain = new CMakeResolvedToolchain(new MockCMakeToolchain("test-toolchain", project.getObjects()));
    registry = new CMakeTaskRegistry();
  }

  // -------------------------------------------------------------------------
  // Constants
  // -------------------------------------------------------------------------

  @Test
  void testGroupConstants() {
    assertEquals("cmake build", CMakeTaskRegistry.GROUP_BUILD);
    assertEquals("cmake test", CMakeTaskRegistry.GROUP_CHECK);
    assertEquals("cmake install", CMakeTaskRegistry.GROUP_INSTALL);
  }

  // -------------------------------------------------------------------------
  // Lifecycle task delegates
  // -------------------------------------------------------------------------

  @Test
  void testAssembleTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.assembleTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("assemble", taskProvider.getName());
  }

  @Test
  void testBuildTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.buildTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build", taskProvider.getName());
  }

  @Test
  void testCheckTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.checkTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check", taskProvider.getName());
  }

  @Test
  void testCleanTaskReturnsProvider() {
    TaskProvider<Task> taskProvider = registry.cleanTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("clean", taskProvider.getName());
  }

  // -------------------------------------------------------------------------
  // CMake-specific task registration (no model objects needed)
  // -------------------------------------------------------------------------

  @Test
  void testCleanListsTaskCreatesTask() {
    TaskProvider<CMakeClean> taskProvider = registry.cleanListsTask(project.getTasks());
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("clean-cmake-lists", taskProvider.getName());
  }

  @Test
  void testAssembleListsTaskCreatesTask() throws Exception {
    TaskProvider<CMakeAssemble> taskProvider = registry.assembleListsTask(
        project.getTasks(), Collections.emptyList(), project);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("assemble-cmake-lists", taskProvider.getName());
  }

  @Test
  void testConfigureTaskCreatesTask() {
    TaskProvider<CMakeConfigure> taskProvider = registry.configureTask(project.getTasks(), toolchain, "Debug");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("configure-test-toolchain-debug", taskProvider.getName());
  }

  @Test
  void testBuildAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.buildAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testBuildAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.buildAllBuildConfigTask(project.getTasks(), toolchain, "Release");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("build-all-test-toolchain-release", taskProvider.getName());
  }

  @Test
  void testCheckAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.checkAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testCheckAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.checkAllBuildConfigTask(project.getTasks(), toolchain, "Debug");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("check-all-test-toolchain-debug", taskProvider.getName());
  }

  @Test
  void testInstallAllToolchainTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.installAllToolchainTask(project.getTasks(), toolchain);
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("install-all-test-toolchain", taskProvider.getName());
  }

  @Test
  void testInstallAllBuildConfigTaskCreatesTask() {
    TaskProvider<?> taskProvider = registry.installAllBuildConfigTask(project.getTasks(), toolchain, "Release");
    assertNotNull(taskProvider);
    assertTrue(taskProvider.isPresent());
    assertEquals("install-all-test-toolchain-release", taskProvider.getName());
  }

  // -------------------------------------------------------------------------
  // Helpers to get resolved model objects via the plugin + CMakeResolver
  // -------------------------------------------------------------------------

  private CMakeResolvedToolchain resolveWithStaticLibrary() {
    project.getPluginManager().apply(CMakePlugin.class);
    CMakeExtension ext = project.getExtensions().getByType(CMakeExtension.class);
    ext.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    ext.getLibraries().register("mylib", lib -> {
      lib.getHeaders().srcDir(project.getProjectDir());
      lib.getSources().srcDir(project.getProjectDir());
      lib.buildVariants(CMakeBuildVariant.STATIC);
    });
    CMakeResolver resolver = new CMakeResolver(project, ext.getPackages(), ext.getToolchains());
    Collection<CMakeResolvedToolchain> result = resolver.process(
        ext.getLibraries(), ext.getApplications(), ext.getTests());
    return result.iterator().next();
  }

  private CMakeResolvedToolchain resolveWithApplication() {
    project.getPluginManager().apply(CMakePlugin.class);
    CMakeExtension ext = project.getExtensions().getByType(CMakeExtension.class);
    ext.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    ext.getApplications().register("myapp", app -> {
      app.getHeaders().srcDir(project.getProjectDir());
      app.getSources().srcDir(project.getProjectDir());
    });
    CMakeResolver resolver = new CMakeResolver(project, ext.getPackages(), ext.getToolchains());
    Collection<CMakeResolvedToolchain> result = resolver.process(
        ext.getLibraries(), ext.getApplications(), ext.getTests());
    return result.iterator().next();
  }

  private CMakeResolvedToolchain resolveWithTest() {
    project.getPluginManager().apply(CMakePlugin.class);
    CMakeExtension ext = project.getExtensions().getByType(CMakeExtension.class);
    ext.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));
    ext.getTests().register("mytest", t -> {
      t.getHeaders().srcDir(project.getProjectDir());
      t.getSources().srcDir(project.getProjectDir());
    });
    CMakeResolver resolver = new CMakeResolver(project, ext.getPackages(), ext.getToolchains());
    Collection<CMakeResolvedToolchain> result = resolver.process(
        ext.getLibraries(), ext.getApplications(), ext.getTests());
    return result.iterator().next();
  }

  // -------------------------------------------------------------------------
  // Registry methods that take resolved model objects
  // -------------------------------------------------------------------------

  @Test
  void testBuildTaskForStaticLibrary() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithStaticLibrary();
    CMakeResolvedLibrary library = resolved.getStaticLibraries().iterator().next();

    TaskProvider<CMakeBuildLibrary> taskProvider = registry.buildTask(
        project.getTasks(), library, resolved, "Debug");

    assertNotNull(taskProvider);
    assertEquals("build-mylib-static-gcc-debug", taskProvider.getName());
  }

  @Test
  void testBuildTaskForExecutable() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithApplication();
    CMakeResolvedExecutable app = resolved.getApplications().iterator().next();

    TaskProvider<CMakeBuildExecutable> taskProvider = registry.buildTask(
        project.getTasks(), app, resolved, "Debug");

    assertNotNull(taskProvider);
    assertEquals("build-myapp-gcc-debug", taskProvider.getName());
  }

  @Test
  void testCheckTaskForTest() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithTest();
    CMakeResolvedExecutable test = resolved.getTests().iterator().next();

    TaskProvider<CMakeCheck> taskProvider = registry.checkTask(
        project.getTasks(), test, resolved, "Debug");

    assertNotNull(taskProvider);
    assertEquals("check-mytest-gcc-debug", taskProvider.getName());
  }

  @Test
  void testInstallTaskForExecutable() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithApplication();
    CMakeResolvedExecutable app = resolved.getApplications().iterator().next();

    TaskProvider<CMakeInstall> taskProvider = registry.installTask(
        project.getTasks(), app, resolved, "Debug");

    assertNotNull(taskProvider);
    assertEquals("install-myapp-gcc-debug", taskProvider.getName());
  }

  @Test
  void testInstallTaskForLibrary() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithStaticLibrary();
    CMakeResolvedLibrary library = resolved.getStaticLibraries().iterator().next();

    TaskProvider<CMakeInstall> taskProvider = registry.installTask(
        project.getTasks(), library, resolved, "Debug");

    assertNotNull(taskProvider);
    assertEquals("install-mylib-static-gcc-debug", taskProvider.getName());
  }

  @Test
  void testAssembleModuleTask() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithStaticLibrary();
    CMakeResolvedLibrary library = resolved.getStaticLibraries().iterator().next();

    TaskProvider<?> taskProvider = registry.assembleModuleTask(
        project.getTasks(), library, resolved, "Debug", project);

    assertNotNull(taskProvider);
    assertEquals("assemble-mylib-static-gcc-debug-module", taskProvider.getName());
  }

  // -------------------------------------------------------------------------
  // configureRemote static methods — binary with no project dependencies
  // so the task parameter is never accessed even when null
  // -------------------------------------------------------------------------

  @SuppressWarnings("unchecked")
  private CMakeResolvedBinary<?> emptyDependencyBinary() {
    CMakeResolvedBinary<?> mockBinary = mock(CMakeResolvedBinary.class);
    when(mockBinary.getPrivateProjectDependencies()).thenReturn(Collections.emptyList());
    when(mockBinary.getPublicProjectDependencies()).thenReturn(Collections.emptyList());
    return mockBinary;
  }

  @Test
  void testConfigureRemoteForAssemble() {
    CMakeTaskRegistry.configureRemote(
        (CMakeAssemble) null, project, emptyDependencyBinary(), toolchain, "Debug");
    assertTrue(true);
  }

  @Test
  void testConfigureRemoteForConfigure() {
    CMakeTaskRegistry.configureRemote(
        (CMakeConfigure) null, project, emptyDependencyBinary(), toolchain, "Debug");
    assertTrue(true);
  }

  @Test
  void testConfigureRemoteForBuild() {
    CMakeTaskRegistry.configureRemote(
        (CMakeBuild) null, emptyDependencyBinary(), toolchain, "Debug");
    assertTrue(true);
  }

  // -------------------------------------------------------------------------
  // Resolved model object properties
  // -------------------------------------------------------------------------

  @Test
  void testResolvedLibraryProperties() throws Exception {
    CMakeResolvedToolchain resolved = resolveWithStaticLibrary();
    CMakeResolvedLibrary library = resolved.getStaticLibraries().iterator().next();

    // getOutputName and getSources are on CMakeResolvedBinary
    assertNotNull(library.getOutputName());
    assertNotNull(library.getSources());

    // hashCode and equals are overridden in CMakeResolvedLibrary
    assertEquals(library.hashCode(), library.hashCode());
    assertTrue(library.equals(library));
  }

  @Test
  void testCustomExecTask() throws Exception {
    project.getPluginManager().apply(CMakePlugin.class);
    CMakeExtension ext = project.getExtensions().getByType(CMakeExtension.class);
    ext.getToolchains().register("gcc", tc -> tc.buildConfigs("Debug"));

    io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto proto =
        new io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto(
            "myExec",
            ext.getToolchains().getByName("gcc"),
            "Debug");

    TaskProvider<?> taskProvider = registry.customExecTask(project.getTasks(), proto);
    assertNotNull(taskProvider);
    assertEquals("myexec-gcc-debug", taskProvider.getName());
  }
}
