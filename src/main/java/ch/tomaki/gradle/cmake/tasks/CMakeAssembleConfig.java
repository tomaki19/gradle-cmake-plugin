package ch.tomaki.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import ch.tomaki.gradle.cmake.files.CMakeConfigFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;

public abstract class CMakeAssembleConfig extends DefaultTask {

  private final Project project;
  private final CMakeResolvedBuild build;

  @Inject
  public CMakeAssembleConfig(final CMakeResolvedBuild build, final Project project) {
    this.project = project;
    this.build = build;
  }

  @TaskAction
  protected void assemble() throws FileNotFoundException, IOException {
    try (final CMakeConfigFile cMakeConfigFile = new CMakeConfigFile(project)) {
      cMakeConfigFile.write(build, project);
    }
  }

}
