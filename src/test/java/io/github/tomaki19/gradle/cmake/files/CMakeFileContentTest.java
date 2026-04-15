/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeFileContentTest {

  @Test
  void testGetName() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeFileContent content = new CMakeFileContent("TestName", project) {

        @Override
        public void writeTo(FileOutputStream outputStream) throws IOException {
          // No-op for testing
        }
      };
      assertEquals("TestName", content.getName());
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testGetProjectName() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeFileContent content = new CMakeFileContent("Test", project) {

        @Override
        public void writeTo(FileOutputStream outputStream) throws IOException {
          // No-op for testing
        }
      };
      assertEquals(project.getName(), content.getProjectName());
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testGetProjectDirectory() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeFileContent content = new CMakeFileContent("Test", project) {

        @Override
        public void writeTo(FileOutputStream outputStream) throws IOException {
          // No-op for testing
        }
      };
      assertNotNull(content.getProjectDirectory());
    } finally {
      deleteRecursively(tempDir);
    }
  }

  @Test
  void testGetBuildDirectory() throws IOException {
    File tempDir = new File(System.getProperty("java.io.tmpdir"), "cmake-test-" + System.currentTimeMillis());
    if (!tempDir.mkdirs()) {
      throw new IOException("Failed to create directory: " + tempDir.getAbsolutePath());
    }
    try {
      org.gradle.api.Project project = ProjectBuilder.builder().withProjectDir(tempDir).build();
      CMakeFileContent content = new CMakeFileContent("Test", project) {

        @Override
        public void writeTo(FileOutputStream outputStream) throws IOException {
          // No-op for testing
        }
      };
      assertNotNull(content.getBuildDirectoryProperty());
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
    assertTrue(file.delete() || !file.exists());
  }

}
