/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedToolchain extends CMakeResolvedNamedObject {

  private final OperatingSystem operatingSystem;
  private final String architecture;
  private final String compiler;
  private final String generator;
  private final Set<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<File> toolchainFile;

  public CMakeResolvedToolchain(final CMakeToolchain toolchain) {
    super(toolchain.getName());
    this.operatingSystem = toolchain.getOperatingSystem().get();
    this.architecture = toolchain.getArchitecture().getOrElse("").toLowerCase();
    this.compiler = toolchain.getCompiler().getOrElse("").toLowerCase();
    this.generator = toolchain.getGenerator().getOrElse("");
    this.buildConfigs = toolchain.getBuildConfigs().get();
    this.environment = toolchain.getEnvironment().getOrNull();
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
    this.toolchainFile = Optional.ofNullable(toolchain.getToolchainFile().getOrNull());
  }

  public String getCompiler() {
    return compiler;
  }

  public OperatingSystem getOperatingSystem() {
    return operatingSystem;
  }

  public String getArchitecture() {
    return architecture;
  }

  public String getGenerator() {
    return generator;
  }

  public Set<String> getBuildConfigs() {
    return buildConfigs;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  public Optional<File> getToolchainFile() {
    return toolchainFile;
  }

}
