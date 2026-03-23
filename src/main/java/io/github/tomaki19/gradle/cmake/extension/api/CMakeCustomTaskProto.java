/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Optional;

import org.gradle.api.file.RegularFile;

public final class CMakeCustomTaskProto {

  private final String name;
  private final CMakeToolchain toolchain;
  private final String buildConfig;
  private final Optional<RegularFile> environmentFile;

  public CMakeCustomTaskProto(final String name, final CMakeToolchain toolchain, final String buildConfig) {
    this.name = name;
    this.toolchain = toolchain;
    this.buildConfig = buildConfig;
    this.environmentFile = Optional.ofNullable(toolchain.getEnvironmentFile().getOrNull());
  }

  public String getName() {
    return name;
  }

  public CMakeToolchain getToolchain() {
    return toolchain;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  public Optional<RegularFile> getEnvironmentFile() {
    return environmentFile;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
    result = prime * result + ((buildConfig == null) ? 0 : buildConfig.hashCode());
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
    CMakeCustomTaskProto other = (CMakeCustomTaskProto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
