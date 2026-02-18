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
    super(names);
  }

  public CMakeLibraryDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeLibraryDependencies getLinkStatic() {
    setLinkage(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeLibraryDependencies getLinkShared() {
    setLinkage(CMakeLinkType.SHARED);
    return this;
  }

  public CMakeLibraryDependencies getLinkInterface() {
    setLinkage(CMakeLinkType.INTERFACE);
    return this;
  }

  public Optional<CMakeLinkType> getBuildType() {
    return buildType;
  }

  public CMakeLibraryDependencies getForStaticBuild() {
    this.buildType = Optional.of(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeLibraryDependencies getForSharedBuild() {
    this.buildType = Optional.of(CMakeLinkType.SHARED);
    return this;
  }

}
