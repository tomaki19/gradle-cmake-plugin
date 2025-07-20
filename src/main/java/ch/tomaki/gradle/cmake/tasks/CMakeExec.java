/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.internal.os.OperatingSystem;

abstract class CMakeExec extends AbstractExecTask<CMakeExec> {

  @org.gradle.api.tasks.Internal
  protected final String toolchainName;

  @org.gradle.api.tasks.Internal
  protected final Optional<File> environmentFile;

  @javax.inject.Inject
  CMakeExec(final String toolchainName, final Optional<File> environmentFile) {
    super(CMakeExec.class);
    this.environmentFile = environmentFile;
    this.toolchainName = toolchainName;
  }

  @org.gradle.api.tasks.Input
  protected abstract SetProperty<String> getBaseCommandLine();

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
