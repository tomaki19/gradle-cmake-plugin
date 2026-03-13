/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Optional;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

public class CMakeLibraryDependencies extends CMakeBinaryDependencies {

  private Optional<CMakeLinkType> buildType = Optional.empty();

  public CMakeLibraryDependencies(final CharSequence... names) {
    super(false, names);
  }

  public CMakeLibraryDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeLibraryDependencies linkStatic() {
    setLinkType(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeLibraryDependencies linkShared() {
    setLinkType(CMakeLinkType.SHARED);
    return this;
  }

  public CMakeLibraryDependencies linkInterface() {
    setLinkType(CMakeLinkType.INTERFACE);
    return this;
  }

  public Optional<CMakeLinkType> getBuildType() {
    return buildType;
  }

  public CMakeLibraryDependencies forStaticBuild() {
    this.buildType = Optional.of(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeLibraryDependencies forSharedBuild() {
    this.buildType = Optional.of(CMakeLinkType.SHARED);
    return this;
  }

  public CMakeLibraryDependencies setPrivate() {
    setVisibilityPrivate(true);
    return this;
  }

  public CMakeLibraryDependencies setPublic() {
    setVisibilityPrivate(false);
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
