
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  public CMakeExec() {
    super(CMakeExec.class);
  }

  @Input
  public abstract SetProperty<String> getBaseCommandLine();

  @Optional
  @Input
  public abstract SetProperty<String> getAdditionalArguments();

  @Optional
  @InputFile
  public abstract RegularFileProperty getEnvironmentFile();

  @Override
  protected void exec() {
    final List<String> commandLine = new ArrayList<>();
    if (getEnvironmentFile().isPresent()) {
      commandLine.add(".");
      commandLine.add(getEnvironmentFile().get().getAsFile().getAbsolutePath());
      commandLine.add("&&");
    }
    commandLine.addAll(getBaseCommandLine().get());
    commandLine.addAll(getAdditionalArguments().get());
    if (OperatingSystem.current().isUnix()) {
      setCommandLine("sh", "-c", String.join(" ", commandLine));
    } else {
      setCommandLine("cmd", "/c", String.join(" ", commandLine));
    }
    super.exec();
  }
}
