/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.CMakeObject;

public abstract class CMakeResolvedObject {

  private final String name;
  private final Set<String> includes;

  CMakeResolvedObject(final CMakeObject object) throws IllegalArgumentException {
    this.name = object.getName();
    this.includes = new HashSet<>(object.getIncludes().get());
  }

  public String getName() {
    return name;
  }

  public Set<String> getIncludes() {
    return includes;
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
    CMakeResolvedObject other = (CMakeResolvedObject) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
