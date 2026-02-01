/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;

public abstract class CMakeFileContent {

  private final String projectName;
  private final Directory projectDirectory;
  private final Directory buildDirectory;

  private static final int INDENT_SIZE = 4;

  public CMakeFileContent(final Project project) throws FileNotFoundException {
    this.projectName = project.getName();
    this.projectDirectory = project.getLayout().getProjectDirectory();
    this.buildDirectory = project.getLayout().getBuildDirectory().get();
  }

  protected String getProjectName() {
    return projectName;
  }

  protected Directory getProjectDirectory() {
    return projectDirectory;
  }

  protected Directory getBuildDirectory() {
    return buildDirectory;
  }

  public abstract void writeTo(final FileOutputStream outputStream) throws IOException;

  private void writeOutput(final FileOutputStream outputStream, final String input) throws IOException {
    outputStream.write(input.getBytes(StandardCharsets.UTF_8));
  }

  protected void writeLine(final FileOutputStream outputStream) throws IOException {
    writeOutput(outputStream, System.lineSeparator());
  }

  protected void write(final FileOutputStream outputStream, final String input, final Object... parameter)
      throws IOException {
    writeOutput(outputStream, input.formatted(parameter));
    writeLine(outputStream);
  }

  protected void write(final FileOutputStream outputStream, final int indent, final String input,
      final Object... parameter) throws IOException {
    writeOutput(outputStream, input.formatted(parameter).indent(indent * INDENT_SIZE));
  }

}
