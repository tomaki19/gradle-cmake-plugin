/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks;

  @javax.inject.Inject
  public CMakeExtension(
      final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks) {
    this.customTasks = customTasks;
  }

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakePackage> getPackages();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

  public void register(final String taskName, final Action<CMakeCustomTaskProto> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCMakeCustomTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
      }
    }
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakeCustomTaskProto> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          putCMakeCustomTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
        }
      }
    }
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakeCustomTaskProto> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCMakeCustomTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
          }
        }
      }
    }
  }

  private void putCMakeCustomTaskProto(final CMakeCustomTaskProto proto, final Action<CMakeCustomTaskProto> action) {
    final String toolchainName = proto.getToolchain().getName();
    if (!customTasks.containsKey(toolchainName)) {
      customTasks.put(toolchainName, new HashMap<>());
    }
    customTasks.get(toolchainName).put(proto, action);
  }

}
