/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.tasks.Exec;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeCoveragePostTestExec extends Exec {

  @Inject
  public CMakeCoveragePostTestExec() {
    final List<String> command = new ArrayList<>();
    command.add("lcov");
    command.add("--no-external");
    command.add("--capture");
    command.add("--directory");
    command.add(getProject().getLayout().getProjectDirectory().getAsFile().toURI().getPath());
    command.add("--output-file");
    final String coverageResultPath = "reports/coverage/%s-post.info".formatted(getProject().getName());
    command.add(getProject().getLayout().getBuildDirectory()
        .dir(coverageResultPath).get().getAsFile().getAbsolutePath());
    if (OperatingSystem.current().isUnix()) {
      commandLine("sh", "-c", String.join(" ", command));
    } else {
      commandLine("cmd", "/c", String.join(" ", command));
    }
  }
}
