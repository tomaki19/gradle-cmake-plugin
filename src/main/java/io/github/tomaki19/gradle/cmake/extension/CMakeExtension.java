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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomPackageTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackageType;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageDevelopment;
import io.github.tomaki19.gradle.cmake.tasks.CMakePackageRuntime;

public abstract class CMakeExtension {

  public static final String NAME = "cmake";

  private final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtos;
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> customPackageRuntimeTaskProtos;
  private final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> customPackageDevelopmentTaskProtos;

  @javax.inject.Inject
  public CMakeExtension(final Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomExec>>> customTaskProtos,
      final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageRuntime>>> customPackageRuntimeTaskProtos,
      final Map<String, Map<CMakeCustomPackageTaskProto, Action<CMakePackageDevelopment>>> customPackageDevelopmentTaskProtos) {
    this.customTaskProtos = customTaskProtos;
    this.customPackageRuntimeTaskProtos = customPackageRuntimeTaskProtos;
    this.customPackageDevelopmentTaskProtos = customPackageDevelopmentTaskProtos;
  }

  public abstract NamedDomainObjectContainer<CMakeToolchain> getToolchains();

  public abstract NamedDomainObjectContainer<CMakePackage> getPackages();

  public abstract NamedDomainObjectContainer<CMakeLibrary> getLibraries();

  public abstract NamedDomainObjectContainer<CMakeApplication> getApplications();

  public abstract NamedDomainObjectContainer<CMakeTest> getTests();

  public void register(final String taskName, final Action<CMakeCustomExec> taskAction) {
    registerExec(taskName, taskAction);
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakeCustomExec> taskAction) {
    registerExec(taskName, toolChainNames, taskAction);
  }

  public void register(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakeCustomExec> taskAction) {
    registerExec(taskName, toolChainNames, buildConfigs, taskAction);
  }

  public void registerExec(final String taskName, final Action<CMakeCustomExec> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCustomExecTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
      }
    }
  }

  public void registerExec(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakeCustomExec> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          putCustomExecTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
        }
      }
    }
  }

  public void registerExec(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakeCustomExec> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCustomExecTaskProto(new CMakeCustomTaskProto(taskName, toolchain, buildConfig), taskAction);
          }
        }
      }
    }
  }

  private void putCustomExecTaskProto(final CMakeCustomTaskProto proto, final Action<CMakeCustomExec> action) {
    final String toolchainName = proto.getToolchain().getName();
    if (!customTaskProtos.containsKey(toolchainName)) {
      customTaskProtos.put(toolchainName, new HashMap<>());
    }
    customTaskProtos.get(toolchainName).put(proto, action);
  }

  public void registerPackageDevelopment(final String taskName, final Action<CMakePackageDevelopment> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCustomPackageDevelopmentTaskProto(
            new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.DEVELOPMENT),
            taskAction);
      }
    }
  }

  public void registerPackageDevelopment(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakePackageDevelopment> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          putCustomPackageDevelopmentTaskProto(
              new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.DEVELOPMENT),
              taskAction);
        }
      }
    }
  }

  public void registerPackageDevelopment(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakePackageDevelopment> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCustomPackageDevelopmentTaskProto(
                new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.DEVELOPMENT),
                taskAction);
          }
        }
      }
    }
  }

  public void registerPackageDevelopmentForComponents(final String taskName,
      final Collection<String> componentNames, final Action<CMakePackageDevelopment> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCustomPackageDevelopmentTaskProto(
            new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, componentNames,
                CMakePackageType.DEVELOPMENT),
            taskAction);
      }
    }
  }

  public void registerPackageDevelopmentForComponents(final String taskName,
      final Collection<String> componentNames, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakePackageDevelopment> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCustomPackageDevelopmentTaskProto(
                new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, componentNames,
                    CMakePackageType.DEVELOPMENT),
                taskAction);
          }
        }
      }
    }
  }

  private void putCustomPackageDevelopmentTaskProto(final CMakeCustomPackageTaskProto proto,
      final Action<CMakePackageDevelopment> action) {
    final String toolchainName = proto.getToolchain().getName();
    if (!customPackageDevelopmentTaskProtos.containsKey(toolchainName)) {
      customPackageDevelopmentTaskProtos.put(toolchainName, new HashMap<>());
    }
    customPackageDevelopmentTaskProtos.get(toolchainName).put(proto, action);
  }

  public void registerPackageRuntime(final String taskName, final Action<CMakePackageRuntime> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCustomPackageRuntimeTaskProto(
            new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.RUNTIME),
            taskAction);
      }
    }
  }

  public void registerPackageRuntime(final String taskName, final Collection<String> toolChainNames,
      final Action<CMakePackageRuntime> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          putCustomPackageRuntimeTaskProto(
              new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.RUNTIME),
              taskAction);
        }
      }
    }
  }

  public void registerPackageRuntime(final String taskName, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakePackageRuntime> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCustomPackageRuntimeTaskProto(
                new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, CMakePackageType.RUNTIME),
                taskAction);
          }
        }
      }
    }
  }

  public void registerPackageRuntimeForComponents(final String taskName,
      final Collection<String> componentNames, final Action<CMakePackageRuntime> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      for (final String buildConfig : toolchain.getBuildConfigs()) {
        putCustomPackageRuntimeTaskProto(
            new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, componentNames,
                CMakePackageType.RUNTIME),
            taskAction);
      }
    }
  }

  public void registerPackageRuntimeForComponents(final String taskName,
      final Collection<String> componentNames, final Collection<String> toolChainNames,
      final Collection<String> buildConfigs, final Action<CMakePackageRuntime> taskAction) {
    for (final CMakeToolchain toolchain : getToolchains()) {
      if (toolChainNames.contains(toolchain.getName())) {
        for (final String buildConfig : toolchain.getBuildConfigs()) {
          if (buildConfigs.contains(buildConfig)) {
            putCustomPackageRuntimeTaskProto(
                new CMakeCustomPackageTaskProto(taskName, toolchain, buildConfig, componentNames,
                    CMakePackageType.RUNTIME),
                taskAction);
          }
        }
      }
    }
  }

  private void putCustomPackageRuntimeTaskProto(final CMakeCustomPackageTaskProto proto,
      final Action<CMakePackageRuntime> action) {
    final String toolchainName = proto.getToolchain().getName();
    if (!customPackageRuntimeTaskProtos.containsKey(toolchainName)) {
      customPackageRuntimeTaskProtos.put(toolchainName, new HashMap<>());
    }
    customPackageRuntimeTaskProtos.get(toolchainName).put(proto, action);
  }

}
