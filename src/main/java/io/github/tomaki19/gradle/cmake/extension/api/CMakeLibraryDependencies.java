/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeLibraryDependencies extends CMakeBinaryDependencies {

  private CMakeBuildVariant buildVariant;

  public CMakeLibraryDependencies(final CharSequence... names) {
    super(CMakeLinkVariant.SHARED, CMakeVisibility.PUBLIC, names);
    buildVariant = CMakeBuildVariant.SHARED;
  }

  public CMakeLibraryDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeBuildVariant getBuildVariant() {
    return buildVariant;
  }

  public CMakeLibraryDependencies forBuildVariant(final CMakeBuildVariant variant) {
    this.buildVariant = variant;
    return this;
  }

  public CMakeLibraryDependencies variant(final CMakeLinkVariant variant) {
    setLinkVariant(variant);
    return this;
  }

  public CMakeLibraryDependencies visibility(final CMakeVisibility visibility) {
    setVisibility(visibility);
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((buildVariant == null) ? 0 : buildVariant.hashCode());
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
    CMakeLibraryDependencies other = (CMakeLibraryDependencies) obj;
    if (buildVariant == null) {
      if (other.buildVariant != null)
        return false;
    } else if (!buildVariant.equals(other.buildVariant))
      return false;
    return true;
  }

}
