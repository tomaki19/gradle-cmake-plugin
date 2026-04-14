/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeTest;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

class CMakeListsFileTest {

  @Test
  void testName() {
    assertEquals("CMakeLists.txt", CMakeListsFile.NAME);
  }

  @Test
  void testConstructor() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project);
      assertNotNull(file);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteTo() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project);

      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }

      // Verify file was created
      assertNotNull(outputFile);
      assertTrue(outputFile.exists());
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithInterfaceLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeInterfaceLibrary.register("InterfaceLib0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithStaticLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("StaticLib0", extension, CMakeBuildVariant.STATIC);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithSharedLibrary() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeBinaryLibrary.register("SharedLib0", extension, CMakeBuildVariant.SHARED);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithApplication() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeApplication.register("App0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteToWithTest() throws IOException, URISyntaxException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
      CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
          CMakeExtension.class, customTasks);

      TestCMakeToolchain.register("Toolchain0", extension);
      TestCMakeTest.register("Test0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeListsFile file = new CMakeListsFile(results, project);
      File outputFile = new File(tempDir, "CMakeLists.txt");
      try (FileOutputStream fos = new FileOutputStream(outputFile)) {
        file.writeTo(fos);
      }
      assertTrue(outputFile.exists());
      assertTrue(outputFile.length() > 0);
    } finally {
      deleteRecursively(tempDir);
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
    // Use assertTrue to check deletion result, satisfying SpotBugs requirement
    assertTrue(file.delete() || !file.exists());
  }

  private void assertTrue(boolean condition) {
    if (!condition) {
      throw new AssertionError("Expected true but was false");
    }
  }
}
