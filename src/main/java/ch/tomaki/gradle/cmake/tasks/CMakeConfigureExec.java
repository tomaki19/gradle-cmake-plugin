package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.file.Directory;
import org.gradle.api.tasks.Exec;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeConfigureExec extends Exec {

  @Inject
  public CMakeConfigureExec(final CMakeResolvedToolchain toolchain) {
    final Directory outputDirectory = getProject().getLayout().getBuildDirectory()
        .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, toolchain.getName())).get();
    // tasks with same output directory are not run in parallel
    getOutputs().dir(outputDirectory);
    final List<String> command = new ArrayList<>();
    if (toolchain.getEnvironmentFile().isPresent()) {
      command.add(".");
      command.add(toolchain.getEnvironmentFile().get().getAbsolutePath());
      command.add("&&");
    }
    command.add("cmake");
    command.add("-S %s".formatted(getProject().getLayout().getProjectDirectory().getAsFile().getAbsolutePath()));
    command.add("-B %s".formatted(outputDirectory.getAsFile().getAbsolutePath()));
    command.add("-G \"%s\"".formatted(toolchain.getGenerator()));
    if (toolchain.getToolchainFile().isPresent()) {
      command.add("--toolchain");
      command.add(" \"%s\"".formatted(
          toolchain.getToolchainFile().get().getAsFile().getAbsolutePath()));
    }
    if (!toolchain.getBuildConfigs().isEmpty()) {
      command.add("-DCMAKE_CONFIGURATION_TYPES=\"%s\"".formatted(
          String.join(";", toolchain.getBuildConfigs())));
    }
    if (OperatingSystem.current().isUnix()) {
      commandLine("sh", "-c", String.join(" ", command));
    } else {
      commandLine("cmd", "/c", String.join(" ", command));
    }
  }

}
