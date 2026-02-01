/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import io.github.tomaki19.gradle.cmake.files.CMakeFileContent;

public abstract class CMakeAssemble extends DefaultTask {

  private final CMakeFileContent content;
  private final File outputFile;

  @javax.inject.Inject
  public CMakeAssemble(final CMakeFileContent content, final File outputFile) {
    this.content = content;
    this.outputFile = outputFile;
    // setOnlyIf((action) -> true);
    getOutputs().file(outputFile);
  }

  @TaskAction
  protected void assemble() throws IOException {
    try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
      content.writeTo(outputStream);
      outputStream.flush();
    }
  }

}
