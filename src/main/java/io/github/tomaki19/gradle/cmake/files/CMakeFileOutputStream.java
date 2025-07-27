/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.file.RegularFile;

public abstract class CMakeFileOutputStream implements AutoCloseable {

  private final RegularFile file;
  private final FileOutputStream outputStream;

  private static final int INDENT_SIZE = 4;

  public CMakeFileOutputStream(final RegularFile regularFile) throws FileNotFoundException {
    this.file = regularFile;
    file.getAsFile().getParentFile().mkdirs();
    this.outputStream = new FileOutputStream(file.getAsFile());
  }

  public RegularFile getFile() {
    return file;
  }

  public abstract void write() throws IOException;

  private void writeOutput(final String input) throws IOException {
    outputStream.write(input.getBytes());
  }

  protected void writeLine() throws IOException {
    writeOutput(System.lineSeparator());
  }

  protected void write(final String input, final Object... parameter) throws IOException {
    writeOutput(input.formatted(parameter));
    writeLine();
  }

  protected void write(final int indent, final String input, final Object... parameter)
      throws IOException {
    writeOutput(input.formatted(parameter).indent(indent * INDENT_SIZE));
  }

  @Override
  public void close() throws IOException {
    outputStream.flush();
    outputStream.close();
  }
}
