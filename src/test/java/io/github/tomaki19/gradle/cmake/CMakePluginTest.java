/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;

class CMakePluginTest {

  @Test
  void load() {
    final Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(CMakePlugin.class);
    assertNotNull(project.getPlugins().findPlugin("io.github.tomaki19.gradle-cmake-plugin"));
  }

  @Test
  void extension() {
    final Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(CMakePlugin.class);
    assertNotNull(project.getExtensions().getByName("cmake"));
  }

  @Test
  void testPluginConstructor() {
    assertThrows(NullPointerException.class, () -> new CMakePlugin(null));
  }

  @Test
  void testAfterEvaluateWithInterfaceLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLib0", extension);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("clean-cmake-lists"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateWithStaticLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("StaticLib0", extension, CMakeBuildVariant.STATIC);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("build-all-toolchain0"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateWithSharedLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("SharedLib0", extension, CMakeBuildVariant.SHARED);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("build-all-toolchain0"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateWithApplication() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeApplication.register("App0", extension);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("build-all-toolchain0"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateWithTest() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeTest.register("Test0", extension);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("check-all-toolchain0"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateEmpty() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("clean-cmake-lists"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testAfterEvaluateWithAllBinaryTypes() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-plugin-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      project.getPluginManager().apply(CMakePlugin.class);

      CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);
      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLib0", extension);
      TestCMakeBinaryLibrary.register("StaticLib0", extension, CMakeBuildVariant.STATIC);
      TestCMakeBinaryLibrary.register("SharedLib0", extension, CMakeBuildVariant.SHARED);
      TestCMakeApplication.register("App0", extension);
      TestCMakeTest.register("Test0", extension);

      ((ProjectInternal) project).evaluate();

      realizeAllTasks(project);
      assertNotNull(project.getTasks().findByName("assemble-cmake-lists"));
      assertNotNull(project.getTasks().findByName("build-all-toolchain0"));
      assertNotNull(project.getTasks().findByName("check-all-toolchain0"));
    } finally {
      deleteRecursively(tempDir);
    }
  }

  private void realizeAllTasks(Project project) {
    for (String taskName : new java.util.ArrayList<>(project.getTasks().getNames())) {
      project.getTasks().findByName(taskName);
    }
  }

  private void deleteRecursively(File file) {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File child : children) {
          deleteRecursively(child);
        }
      }
    }
    assertTrue(file.delete() || !file.exists());
  }

  private void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected true but was false");
    }
  }
}
