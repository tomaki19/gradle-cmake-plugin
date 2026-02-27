/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.TaskAction;

import io.github.tomaki19.gradle.cmake.files.CMakeFileContent;

public abstract class CMakeAssemble extends DefaultTask {

  private final Collection<? extends CMakeFileContent> fileContents;
  private final Directory outputDir;

  @javax.inject.Inject
  public CMakeAssemble(final Collection<? extends CMakeFileContent> fileContents, final Directory outputDir) {
    this.fileContents = fileContents;
    this.outputDir = outputDir;
    // if gradle build file changes, configure needs to be run
    getInputs().file(getProject().getBuildFile());
  }

  @TaskAction
  protected void assemble() throws IOException {
    for (final CMakeFileContent fileContent : fileContents) {
      final File outputFile = outputDir.file(fileContent.getName()).getAsFile();
      Files.createDirectories(outputFile.getParentFile().toPath());
      try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        fileContent.writeTo(outputStream);
        outputStream.flush();
      }
    }
  }

}
