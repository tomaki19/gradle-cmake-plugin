/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

public final class CMakeCustomPackageTaskProto {

  private final String name;
  private final CMakeToolchain toolchain;
  private final String buildConfig;
  private final Collection<String> componentNames;
  private final CMakePackageType packageType;

  public CMakeCustomPackageTaskProto(final String name, final CMakeToolchain toolchain,
      final String buildConfig, final CMakePackageType packageType) {
    this.name = name;
    this.toolchain = toolchain;
    this.buildConfig = buildConfig;
    this.componentNames = Collections.emptySet();
    this.packageType = packageType;
  }

  public CMakeCustomPackageTaskProto(final String name, final CMakeToolchain toolchain,
      final String buildConfig, final Collection<String> componentNames, final CMakePackageType packageType) {
    this.name = name;
    this.toolchain = toolchain;
    this.buildConfig = buildConfig;
    this.componentNames = Collections.unmodifiableSet(new LinkedHashSet<>(componentNames));
    this.packageType = packageType;
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

  public CMakePackageType getPackageType() {
    return packageType;
  }

  public boolean isComponentScoped() {
    return !componentNames.isEmpty();
  }

  public boolean matchesComponent(final String name) {
    return componentNames.isEmpty() || componentNames.contains(name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, toolchain, buildConfig, componentNames, packageType);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeCustomPackageTaskProto other))
      return false;
    return Objects.equals(name, other.name)
        && Objects.equals(toolchain, other.toolchain)
        && Objects.equals(buildConfig, other.buildConfig)
        && Objects.equals(componentNames, other.componentNames)
        && packageType == other.packageType;
  }

}
