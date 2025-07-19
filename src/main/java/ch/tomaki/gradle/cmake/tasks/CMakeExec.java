/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.internal.os.OperatingSystem;

abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  protected final String toolchainName;

  @Inject
  public CMakeExec(final String toolchainName, final Optional<File> environmentFile) {
    super(CMakeExec.class);
    environmentFile.ifPresent((file) -> {
      if (file.exists()) {
        getEnvironmentFile().set(file);
      } else {
        throw new IllegalArgumentException("Environment file missing!");
      }
    });
    this.toolchainName = toolchainName;
  }

  @org.gradle.api.tasks.Input
  public abstract SetProperty<String> getBaseCommandLine();

  @org.gradle.api.tasks.Optional
  @org.gradle.api.tasks.Input
  public abstract SetProperty<String> getAdditionalArguments();

  @org.gradle.api.tasks.Optional
  @org.gradle.api.tasks.InputFile
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
