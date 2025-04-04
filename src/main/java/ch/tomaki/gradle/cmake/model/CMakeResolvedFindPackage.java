/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CMakeResolvedFindPackage {

  private final String name;
  private final Set<String> components;
  private final Map<String, String> properties;

  public CMakeResolvedFindPackage(final CMakeFindPackage findPackage) {
    this.name = findPackage.getName();
    this.components = new HashSet<>(findPackage.getComponents().get());
    this.properties = findPackage.getProperties().get();
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CMakeResolvedFindPackage other = (CMakeResolvedFindPackage) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
}
