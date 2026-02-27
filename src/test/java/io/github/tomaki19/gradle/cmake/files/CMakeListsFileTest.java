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

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

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
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project.getName(),
          project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());
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
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeListsFile file = new CMakeListsFile(java.util.Collections.emptyList(), project.getName(),
          project.getLayout().getProjectDirectory(), project.getLayout().getBuildDirectory().get());

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
