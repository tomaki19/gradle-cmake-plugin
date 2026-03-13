/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildType;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeLibraryDependencies extends CMakeBinaryDependencies {

  public final static CMakeBuildType Static = CMakeBuildType.STATIC;
  public final static CMakeBuildType Shared = CMakeBuildType.SHARED;

  private CMakeBuildType buildType;

  public CMakeLibraryDependencies(final CharSequence... names) {
    super(CMakeLinkType.SHARED, CMakeVisibilityType.PUBLIC, names);
    buildType = CMakeBuildType.SHARED;
  }

  public CMakeLibraryDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeLibraryDependencies link(final CMakeLinkType type) {
    setLinkType(type);
    return this;
  }

  public CMakeLibraryDependencies visibility(final CMakeVisibilityType type) {
    setVisibilityType(type);
    return this;
  }

  public CMakeBuildType getBuildType() {
    return buildType;
  }

  public CMakeLibraryDependencies build(final CMakeBuildType type) {
    this.buildType = type;
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((buildType == null) ? 0 : buildType.hashCode());
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
    if (buildType == null) {
      if (other.buildType != null)
        return false;
    } else if (!buildType.equals(other.buildType))
      return false;
    return true;
  }

}
