/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import io.github.tomaki19.gradle.cmake.files.CMakeFileContent;

@CacheableTask
public abstract class CMakeAssemble extends DefaultTask {

  private final CMakeFileContent fileContent;

  @javax.inject.Inject
  public CMakeAssemble(final CMakeFileContent fileContent) {
    this.fileContent = fileContent;
    // if gradle build file changes, configure needs to be run
    getInputs().file(getProject().getBuildFile());
  }

  @Internal
  public abstract DirectoryProperty getOutputDirectory();

  @TaskAction
  protected void assemble() throws IOException {
    final File outputFile = getOutputDirectory().get().file(fileContent.getName()).getAsFile();
    Files.createDirectories(outputFile.getParentFile().toPath());
    try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
      fileContent.writeTo(outputStream);
      outputStream.flush();
    }
  }

}
