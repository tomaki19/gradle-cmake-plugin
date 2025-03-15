package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.tasks.Exec;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;

public abstract class CMakeBuildExec extends Exec {

  @Inject
  public CMakeBuildExec(final String buildTarget, final CMakeResolvedBinary binary) {
    setGroup(CMakeTasksConventions.GROUP_BUILD);
    final List<String> command = new ArrayList<>();
    if (binary.getToolchain().getEnvironmentFile().isPresent()) {
      command.add(".");
      command.add(binary.getToolchain().getEnvironmentFile().get().getAbsolutePath());
      command.add("&&");
    }
    if (!buildTarget.endsWith("interface")) {
      command.add("cmake");
      command.add("--build");
      command.add(getProject().getLayout().getBuildDirectory()
          .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, binary.getToolchain().getName())).get()
          .getAsFile().getAbsolutePath());
      command.add("--target");
      command.add(buildTarget);
      command.add("--config");
      command.add(binary.getBuildConfig());
    }
    if (OperatingSystem.current().isUnix()) {
      commandLine("sh", "-c", String.join(" ", command));
    } else {
      commandLine("cmd", "/c", String.join(" ", command));
    }
  }

}
