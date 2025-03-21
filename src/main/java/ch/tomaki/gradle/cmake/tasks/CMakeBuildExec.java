package ch.tomaki.gradle.cmake.tasks;

import javax.inject.Inject;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

import ch.tomaki.gradle.cmake.files.CMakeListsConventions;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBinary;

public abstract class CMakeBuildExec extends CMakeExec {

  public final String buildTarget;

  @Input
  public abstract SetProperty<String> getAdditionalArguments();

  @Inject
  public CMakeBuildExec(final String buildTarget, final CMakeResolvedBinary binary) {
    setGroup(CMakeTasksConventions.GROUP_BUILD);
    setWorkingDir(getProject().getProjectDir());
    this.buildTarget = buildTarget;
    binary.getToolchain().getEnvironmentFile().ifPresent((file) -> {
      getEnvironmentFile().set(file);
    });
    if (!binary.getSources().isEmpty()) {
      getBaseCommandLine().add("cmake");
      getBaseCommandLine().add("--build");
      getBaseCommandLine().add(getProject().getLayout().getBuildDirectory()
          .dir("%s/%s".formatted(CMakeListsConventions.CMAKE_BUILD_PATH, binary.getToolchain().getName())).get()
          .getAsFile().getAbsolutePath());
      getBaseCommandLine().add("--target");
      getBaseCommandLine().add(buildTarget);
      getBaseCommandLine().add("--config");
      getBaseCommandLine().add(binary.getBuildConfig());
    }
  }

}
