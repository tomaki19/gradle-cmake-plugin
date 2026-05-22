/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary<CMakeResolvedLibrary> {

  private final CMakeLinkVariant linkVariant;

  public CMakeResolvedLibrary(final CMakeLibrary library, final CMakeLinkVariant linkVariant,
      final boolean stripDebug, final String projectVersion) {
    super(library, stripDebug, projectVersion);
    this.linkVariant = linkVariant;
  }

  public CMakeLinkVariant getLinkVariant() {
    return linkVariant;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((linkVariant == null) ? 0 : linkVariant.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeResolvedLibrary other = (CMakeResolvedLibrary) obj;
    if (linkVariant != other.linkVariant)
      return false;
    return true;
  }

}
