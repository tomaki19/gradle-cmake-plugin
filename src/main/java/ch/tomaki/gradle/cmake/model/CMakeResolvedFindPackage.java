/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;

public final class CMakeResolvedFindPackage {

  private final String name;
  private final Set<String> components;
  private final Map<String, String> properties;
  private final Optional<CMakeResolvedToolchain> toolchain;

  CMakeResolvedFindPackage(final CMakeFindPackage findPackage, final Optional<CMakeResolvedToolchain> toolchain) {
    this.name = findPackage.getName();
    this.components = new HashSet<>(findPackage.getComponents().get());
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

  public Optional<CMakeResolvedToolchain> getToolchain() {
    return toolchain;
  }

  static void resolveFindPackageDependencies(final Set<CMakeResolvedFindPackage> findPackages,
      final Set<CMakeResolvedFindPackageDependency> findPackageDependencies,
      final Optional<CMakeResolvedToolchain> toolchain, final Map<String, CMakeFindPackage> availableFindPackages,
      final Set<String> dependencies)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (availableFindPackages.containsKey(dependencyTokens[0])) {
            final CMakeFindPackage findPackage = availableFindPackages.get(dependencyTokens[0]);
            findPackages.add(new CMakeResolvedFindPackage(findPackage, toolchain));
            findPackageDependencies.add(new CMakeResolvedFindPackageDependency(dependency));
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
    CMakeResolvedFindPackage other = (CMakeResolvedFindPackage) obj;
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
