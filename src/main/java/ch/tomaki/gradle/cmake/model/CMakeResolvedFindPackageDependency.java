/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;

public final class CMakeResolvedFindPackageDependency {

  private final String findPackageName;
  private final String identifier;
  private final CMakeResolvedToolchain toolchain;

  public CMakeResolvedFindPackageDependency(final CMakeFindPackage findPackage, final CMakeResolvedToolchain toolchain,
      final String identifier) {
    this.findPackageName = findPackage.getName();
    this.toolchain = toolchain;
    this.identifier = identifier;
  }

  public String getFindPackageName() {
    return findPackageName;
  }

  public CMakeResolvedToolchain getToolchain() {
    return toolchain;
  }

  public String getIdentifier() {
    return identifier;
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
