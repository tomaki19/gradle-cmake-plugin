package ch.tomaki.gradle.cmake.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import ch.tomaki.gradle.cmake.files.CMakeListsFile;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;

public abstract class CMakeAssembleLists extends DefaultTask {

  private final Project project;
  private final CMakeResolvedBuild build;

  @Inject
  public CMakeAssembleLists(final CMakeResolvedBuild build, final Project project) {
    this.project = project;
    this.build = build;
  }

  @TaskAction
  protected void assemble() throws FileNotFoundException, IOException {
    try (final CMakeListsFile cMakeListsFile = new CMakeListsFile(project.getLayout().getProjectDirectory())) {
      cMakeListsFile.write(build, project);
    }
  }

}
