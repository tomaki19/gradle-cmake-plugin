/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.io.File;
import java.util.Optional;

import org.gradle.api.Named;

public final class CMakeCustomTaskProto implements Named {

  private final String name;
  private final String toolchainName;
  private final String buildConfig;
  private final Optional<File> environmentFile;

  public CMakeCustomTaskProto(final String name, final CMakeToolchain toolchain, final String buildConfig) {
    this.name = name;
    this.toolchainName = toolchain.getName();
    this.buildConfig = buildConfig;
    this.environmentFile = toolchain.getEnvironmentFile();
  }

  @Override
  public String getName() {
    return name;
  }

  public String getToolchainName() {
    return toolchainName;
  }

  public String getBuildConfig() {
    return buildConfig;
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((toolchainName == null) ? 0 : toolchainName.hashCode());
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
    if (toolchainName == null) {
      if (other.toolchainName != null)
        return false;
    } else if (!toolchainName.equals(other.toolchainName))
      return false;
    if (buildConfig == null) {
      if (other.buildConfig != null)
        return false;
    } else if (!buildConfig.equals(other.buildConfig))
      return false;
    return true;
  }

}
