package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.tasks.Exec;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedTest;

public class CMakeTestExec extends Exec {

  @Inject
  public CMakeTestExec(final String buildTarget, final CMakeResolvedTest test) {
    setGroup(CMakeTasksConventions.GROUP_CHECK);
    final List<String> command = new ArrayList<>();
    command.add("ctest");
    command.add("--tests-regex");
    command.add(buildTarget);
    command.add("--test-dir");
    command.add(getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, test.getToolchain().getName())).get().getAsFile()
        .getAbsolutePath());
    command.add("--build-config");
    command.add(test.getBuildConfig());
    if (test.isCreateTestResultsXml()) {
      command.add("--output-junit");
      final String testResultPath = "reports/tests/%s.xml".formatted(buildTarget);
      command.add(getProject().getLayout().getBuildDirectory().dir(testResultPath).get().getAsFile().getAbsolutePath());
    }
    command.add("--output-on-failure");
    command.add("--verbose");
    if (OperatingSystem.current().isUnix()) {
      commandLine("sh", "-c", String.join(" ", command));
    } else {
      commandLine("cmd", "/c", String.join(" ", command));
    }
  }

}
