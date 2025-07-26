/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import io.github.tomaki19.gradle.cmake.files.CMakeFileOutputStream;

public abstract class CMakeAssemble extends DefaultTask {

  private final CMakeFileOutputStream outputStream;

  @javax.inject.Inject
  public CMakeAssemble(final CMakeFileOutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @TaskAction
  protected void assemble() throws IOException {
    outputStream.write();
  }

}
