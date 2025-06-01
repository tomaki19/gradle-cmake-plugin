/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolvedToolchain implements CMakeResolvedNamedObject {

  private final String name;
  private final OperatingSystem operatingSystem;
  private final String architecture;
  private final String compiler;
  private final String generator;
  private final Set<String> buildConfigs;
  private final Map<String, String> environment;
  private final Optional<File> environmentFile;
  private final Optional<File> toolchainFile;

  public CMakeResolvedToolchain(final CMakeToolchain toolchain) {
    this.name = toolchain.getName();
    this.operatingSystem = toolchain.getOperatingSystem().getOrNull();
    this.architecture = toolchain.getArchitecture().getOrElse("").toLowerCase();
    this.compiler = toolchain.getCompiler().getOrElse("").toLowerCase();
    this.generator = toolchain.getGenerator().getOrElse("");
    this.buildConfigs = toolchain.getBuildConfigs().getOrElse(new HashSet<>(Arrays.asList("debug", "release")));
    this.environment = toolchain.getEnvironment().get();
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
    this.toolchainFile = Optional.ofNullable(toolchain.getToolchainFile().getOrNull());
  }

  @Override
  public String getName() {
    return name;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedToolchain other = (CMakeResolvedToolchain) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
