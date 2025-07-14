/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.io.IOException;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import ch.tomaki.gradle.cmake.files.CMakeFileOutputStream;

public abstract class CMakeAssemble extends DefaultTask {

  private final CMakeFileOutputStream outputStream;

  @Inject
  public CMakeAssemble(final CMakeFileOutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @TaskAction
  protected void assemble() throws IOException {
    outputStream.write();
  }
}
