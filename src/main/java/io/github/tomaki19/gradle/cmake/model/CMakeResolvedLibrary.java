/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class CMakeResolvedLibrary extends CMakeResolvedBinary<CMakeResolvedLibrary> {

  private final CMakeLinkType linkType;

  CMakeResolvedLibrary(final CMakeLibrary library, final CMakeLinkType linkType, final boolean stripDebug) {
    super(library, stripDebug);
    this.linkType = linkType;
  }

  public CMakeLinkType getLinkType() {
    return linkType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
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
    if (linkType != other.linkType)
      return false;
    return true;
  }

}
