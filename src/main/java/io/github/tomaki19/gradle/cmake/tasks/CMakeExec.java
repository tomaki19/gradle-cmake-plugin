/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.internal.os.OperatingSystem;

abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  protected final String toolchainName;
  protected final Optional<File> environmentFile;
  protected final String buildConfig;
  protected final List<String> baseArguments = new ArrayList<>();

  @javax.inject.Inject
  CMakeExec(final String toolchainName, final Optional<File> environmentFile, final String buildConfig) {
    super(CMakeExec.class);
    this.toolchainName = toolchainName;
    this.environmentFile = environmentFile;
    this.buildConfig = buildConfig;
  }

  @org.gradle.api.tasks.Input
  protected abstract Property<String> getBaseCommand();

  @org.gradle.api.tasks.Optional
  @org.gradle.api.tasks.Input
  protected abstract SetProperty<String> getAdditionalArguments();

  @Override
  protected void exec() {
    final List<String> commandLine = new ArrayList<>();
    environmentFile.ifPresent((file) -> {
      commandLine.add(".");
      commandLine.add(environmentFile.get().getAbsolutePath());
      commandLine.add("&&");
    });
    commandLine.add(getBaseCommand().get());
    commandLine.addAll(baseArguments);
    commandLine.addAll(getAdditionalArguments().get());
    if (OperatingSystem.current().isUnix()) {
      setCommandLine("sh", "-c", String.join(" ", commandLine));
    } else {
      setCommandLine("cmd", "/c", String.join(" ", commandLine));
    }
    super.exec();
  }
}
