/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;

public final class CMakeResolvedFindPackageDependency {

  private final String identifier;
  private final String packageName;
  private final Set<String> components;
  private final Map<String, String> properties;
  private final CMakeResolvedToolchain toolchain;

  public CMakeResolvedFindPackageDependency(final String identifier, final CMakeFindPackage findPackage,
      final CMakeResolvedToolchain toolchain) {
    this.identifier = identifier;
    this.packageName = findPackage.getName();
    this.components = new HashSet<>(findPackage.getComponents().get());
    this.properties = findPackage.getProperties().get();
    this.toolchain = toolchain;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getPackageName() {
    return packageName;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
    CMakeResolvedFindPackageDependency other = (CMakeResolvedFindPackageDependency) obj;
    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;
    if (toolchain == null) {
      if (other.toolchain != null)
        return false;
    } else if (!toolchain.equals(other.toolchain))
      return false;
    return true;
  }

}
