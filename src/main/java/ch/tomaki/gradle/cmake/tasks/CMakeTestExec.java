package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;

public abstract class CMakeTestExec extends CMakeExec {

  public final String buildTarget;

  @Inject
  public CMakeTestExec(final String buildTarget, final CMakeResolvedTest test) {
    setGroup(CMakeTasksConventions.GROUP_CHECK);
    setWorkingDir(getProject().getProjectDir());
    this.buildTarget = buildTarget;
    test.getToolchain().getEnvironmentFile().ifPresent((file) -> {
      getEnvironemtFile().set(file);
    });
    getBaseCommandLine().add("ctest");
    getBaseCommandLine().add("--tests-regex");
    getBaseCommandLine().add(buildTarget);
    getBaseCommandLine().add("--test-dir");
    getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, test.getToolchain().getName())).get().getAsFile()
        .getAbsolutePath());
    getBaseCommandLine().add("--build-config");
    getBaseCommandLine().add(test.getBuildConfig());
    getBaseCommandLine().add("--output-on-failure");
  }

}
