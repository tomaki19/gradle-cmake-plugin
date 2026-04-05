/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.internal.os.OperatingSystem;

abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  protected final String toolchainName;
  protected final String buildConfig;

  private final Optional<RegularFile> environmentFile;

  @javax.inject.Inject
  CMakeExec(final String toolchainName, final String buildConfig, final Optional<RegularFile> environmentFile) {
    super(CMakeExec.class);
    this.toolchainName = toolchainName;
    this.buildConfig = buildConfig;
    this.environmentFile = environmentFile;
  }

  @Override
  protected void exec() {
    final List<String> commandLine = new ArrayList<>();
    environmentFile.ifPresent((file) -> {
      commandLine.add(".");
      commandLine.add(file.getAsFile().getAbsolutePath());
      commandLine.add("&&");
    });
    commandLine.add(getExecutable());
    commandLine.addAll(getArgs());
    if (OperatingSystem.current().isUnix()) {
      setCommandLine("sh", "-c", String.join(" ", commandLine));
    } else {
      setCommandLine("cmd", "/c", String.join(" ", commandLine));
    }
    super.exec();
  }
}
