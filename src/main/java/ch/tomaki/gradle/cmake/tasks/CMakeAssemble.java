
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import ch.tomaki.gradle.cmake.files.CMakeFileOutputStream;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

public abstract class CMakeAssemble extends DefaultTask {

  private final CMakeFileOutputStream outputStream;
  private final CMakeResolvedBuild build;
  private final Project project;

  @Inject
  public CMakeAssemble(final CMakeFileOutputStream outputStream, final CMakeResolvedBuild build) {
    this.outputStream = outputStream;
    this.build = build;
    this.project = getProject();
  }

  @TaskAction
  protected void assemble() throws FileNotFoundException, IOException {
    outputStream.write(build, project);
  }
}
