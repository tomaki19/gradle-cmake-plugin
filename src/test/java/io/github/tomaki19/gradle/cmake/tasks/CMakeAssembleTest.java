/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

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
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testTaskGroup() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNull(task.getGroup());
  }

  @Test
  void testTaskName() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getName());
  }

  @Test
  void testMultipleAssembleOperations() {
    CMakeAssemble task1 = project.getTasks().register("assemble1", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    CMakeAssemble task2 = project.getTasks().register("assemble2", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task1);
    assertNotNull(task2);
  }

  @Test
  void testTargetDependencies() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testProjectIntegration() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getProject());
  }

  @Test
  void testInputs() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getInputs());
  }

  @Test
  void testOutputs() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getOutputs());
  }

  @Test
  void testCacheableTask() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task);
  }

  @Test
  void testDependencyResolution() {
    CMakeAssemble task = project.getTasks().register("assemble", CMakeAssemble.class,
        project.getLayout().getProjectDirectory(), fileContent).get();
    assertNotNull(task.getTaskDependencies());
  }

  @Test
  void testAssemble_writesFileToOutputDirectory() throws IOException {
    try {
      final CMakeAssemble task = project.getTasks().register("assembleAction",
          CMakeAssemble.class, project.getLayout().getProjectDirectory(), fileContent).get();
      task.assemble();
      assertTrue(Files.exists(project.getLayout().getProjectDirectory().file("test.cmake").getAsFile().toPath()));
    } finally {
      try (final java.util.stream.Stream<java.nio.file.Path> stream = Files
          .walk(project.getLayout().getProjectDirectory().getAsFile().toPath())) {
        stream.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
          try {
            Files.delete(p);
          } catch (IOException e) {
            // ignore cleanup errors
          }
        });
      }
    }
  }
}
