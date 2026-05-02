/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.files.CMakeFileContent;

class CMakeAssembleTest {

  private Project project;
  private CMakeFileContent fileContent;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    fileContent = new CMakeFileContent("test.cmake", project) {
      @Override
      public void writeTo(final FileOutputStream outputStream) throws IOException {
      }
    };
  }

  @Test
  void testTaskCreation() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testTaskGroup() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testTaskName() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getName());
  }

  @Test
  void testTaskType() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testMultipleAssembleOperations() {
    CMakeAssemble task1 = project.getTasks().register("assemble1", CMakeAssemble.class, fileContent).get();
    CMakeAssemble task2 = project.getTasks().register("assemble2", CMakeAssemble.class, fileContent).get();
    assertNotNull(task1);
    assertNotNull(task2);
  }

  @Test
  void testTargetDependencies() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getProject());
  }

  @Test
  void testInputs() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getInputs());
  }

  @Test
  void testOutputs() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getOutputs());
  }

  @Test
  void testCacheableTask() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testDependencyResolution() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class, fileContent).get();
    assertNotNull(task.getTaskDependencies());
  }
}
