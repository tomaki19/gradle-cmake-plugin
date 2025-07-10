/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

public final class CMakeResolvedPackageDependency {

  private final String identifier;

  CMakeResolvedPackageDependency(final String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
    CMakeResolvedPackageDependency other = (CMakeResolvedPackageDependency) obj;
    if (identifier == null) {
      if (other.identifier != null)
        return false;
    } else if (!identifier.equals(other.identifier))
      return false;
    return true;
  }

}
