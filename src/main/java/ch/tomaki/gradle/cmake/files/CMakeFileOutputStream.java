package ch.tomaki.gradle.cmake.files;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gradle.api.file.RegularFile;

public class CMakeFileOutputStream implements AutoCloseable {

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

  private void writeOutput(final String input) throws IOException {
    outputStream.write(input.getBytes());
  }

  public void writeLine() throws IOException {
    writeOutput(System.lineSeparator());
  }

  public void write(final String input, final Object... parameter) throws IOException {
    writeOutput(input.formatted(parameter));
    writeLine();
  }

  public void write(final int indent, final String input, final Object... parameter) throws IOException {
    writeOutput(input.formatted(parameter).indent(indent * INDENT_SIZE));
  }

  @Override
  public void close() throws IOException {
    outputStream.flush();
    outputStream.close();
  }

}
