/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;

public final class CMakeResolvedPackage {

  private final String name;
  private final Set<String> components;
  private final Map<String, String> properties;
  private final CMakeResolvedToolchain toolchain;

  CMakeResolvedPackage(final CMakeFindPackage findPackage, final CMakeResolvedToolchain toolchain) {
    this.name = findPackage.getName();
    this.components = findPackage.getComponents().get();
    this.properties = findPackage.getProperties().get();
    this.toolchain = toolchain;
  }

  public String getName() {
    return name;
  }

  public Set<String> getComponents() {
    return components;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  static void resolvePackageDependencies(final Set<String> dependencies, final Set<CMakeResolvedPackage> packages,
      final Set<CMakeResolvedPackageDependency> packageDependencies, final CMakeResolvedToolchain toolchain,
      final Map<String, CMakeFindPackage> availableFindPackages)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (availableFindPackages.containsKey(dependencyTokens[0])) {
            final CMakeFindPackage findPackage = availableFindPackages.get(dependencyTokens[0]);
            packages.add(new CMakeResolvedPackage(findPackage, toolchain));
            packageDependencies.add(new CMakeResolvedPackageDependency(dependency));
          } else {
            throw new IllegalArgumentException("Missing find package '%s'!".formatted(dependency));
          }
        }
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
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
    CMakeResolvedPackage other = (CMakeResolvedPackage) obj;
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
    return true;
  }

}
