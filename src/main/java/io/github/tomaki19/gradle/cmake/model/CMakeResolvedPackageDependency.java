/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Optional;

public final class CMakeResolvedPackageDependency extends CMakeResolvedName<CMakeResolvedPackageDependency> {

  private final CMakeResolvedPackage resolvedPackage;
  private final Optional<String> targetPrefix;

  CMakeResolvedPackageDependency(final String name, final CMakeResolvedPackage resolvedPackage,
      final Optional<String> targetPrefix) {
    super(name);
    this.resolvedPackage = resolvedPackage;
    this.targetPrefix = targetPrefix;
  }

  public CMakeResolvedPackage getResolvedPackage() {
    return resolvedPackage;
  }

  public String getTargetPrefix() {
    return targetPrefix.orElse(resolvedPackage.getName());
  }

}
