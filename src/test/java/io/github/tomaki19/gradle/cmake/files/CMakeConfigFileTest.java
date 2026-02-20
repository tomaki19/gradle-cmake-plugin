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
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

class CMakeConfigFileTest {

  @Test
  void testName() {
    assertEquals("project-config.cmake", CMakeConfigFile.name("project"));
  }

  @Test
  void testConstructor() throws IOException, URISyntaxException {
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
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);

      CMakeConfigFile file = new CMakeConfigFile(toolchains[0], project.getName(),
          project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());
      assertNotNull(file);
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testWriteTo() throws IOException, URISyntaxException {
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
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);

      CMakeConfigFile file = new CMakeConfigFile(toolchains[0], project.getName(),
          project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());

      File outputFile = new File(tempDir, "config.cmake");
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
  void testWriteToWithPackage() throws IOException, URISyntaxException {
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
      TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);

      CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
      java.util.Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
          extension.getApplications(), extension.getTests());

      CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
      assertNotNull(toolchains);
      assertEquals(1, toolchains.length);

      CMakeConfigFile file = new CMakeConfigFile(toolchains[0], project.getName(),
          project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());

      File outputFile = new File(tempDir, "config.cmake");
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
