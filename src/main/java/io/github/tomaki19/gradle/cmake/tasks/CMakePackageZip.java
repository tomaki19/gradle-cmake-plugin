/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.bundling.Zip;

@CacheableTask
public abstract class CMakePackageZip extends Zip {

  @javax.inject.Inject
  public CMakePackageZip() {
    setGroup(CMakeTaskRegistry.GROUP_PACKAGE);
    getArchiveBaseName().set(getProject().getName());
  }

  // @Input
  // public abstract Property<String> getArchiveBaseName();

  // @Input
  // public abstract Property<Object> getArchiveVersion();

  // @TaskAction
  // protected void doAction() throws FileNotFoundException, IOException {
  // System.out.println("Package: %s-%s.zip"
  // .formatted(getArchiveBaseName().getOrElse(""),
  // getArchiveVersion().getOrElse("")));

  // try (final FileOutputStream fileStream = new FileOutputStream("%s-%s.zip"
  // .formatted(getArchiveBaseName().getOrElse(""),
  // getArchiveVersion().getOrElse("")));
  // final ZipOutputStream zipStream = new ZipOutputStream(fileStream)) {
  // for (final File entry : getInputs().getFiles().getFiles()) {
  // if (entry.isDirectory()) {
  // try (DirectoryStream<Path> directoryStream =
  // Files.newDirectoryStream(entry.toPath())) {
  // for (Path filePath : directoryStream) {
  // if (Files.isRegularFile(filePath)) {
  // System.out.println(" -> " + filePath.toFile().getAbsolutePath());
  // writeData(filePath.toFile(), zipStream);
  // }
  // }
  // }
  // } else {
  // System.out.println(" -> " + entry.getAbsolutePath());
  // writeData(entry, zipStream);
  // }
  // }
  // }
  // }

  // private static void writeData(final File file, final ZipOutputStream
  // zipStream) throws IOException {
  // zipStream.putNextEntry(new ZipEntry(file.getName()));
  // try (final FileInputStream readStream = new FileInputStream(file)) {
  // final byte[] data = new byte[1024];
  // int offset = 0;
  // while (readStream.available() > 0) {
  // final int length = readStream.read(data);
  // System.out.println("read " + length);
  // zipStream.write(data, offset, length);
  // offset += length;
  // System.out.println("offset " + offset);
  // }
  // } finally {
  // zipStream.closeEntry();
  // }
  // }

}
