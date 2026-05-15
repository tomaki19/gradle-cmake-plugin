/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.Directory;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;

import io.github.tomaki19.gradle.cmake.files.CMakeFileContent;

@CacheableTask
public abstract class CMakeAssemble extends DefaultTask {

  private final Directory outpuDirectory;
  private final CMakeFileContent fileContent;

  @javax.inject.Inject
  public CMakeAssemble(final Directory outputDirectory, final CMakeFileContent fileContent) {
    this.outpuDirectory = outputDirectory;
    this.fileContent = fileContent;
    getInputs().file(getProject().getBuildFile());
  }

  @TaskAction
  protected void assemble() throws IOException {
    final Path outputDirectory = outpuDirectory.getAsFile().toPath();
    Files.createDirectories(outputDirectory);
    try (final FileOutputStream outputStream = new FileOutputStream(outpuDirectory.file(fileContent.getName()).getAsFile())) {
      fileContent.writeTo(outputStream);
      outputStream.flush();
    }
  }

}
