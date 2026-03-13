/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

public class CMakeExecutableDependencies extends CMakeBinaryDependencies {

  public CMakeExecutableDependencies(final CharSequence... names) {
    super(true, names);
  }

  public CMakeExecutableDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeExecutableDependencies linkStatic() {
    setLinkType(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeExecutableDependencies linkShared() {
    setLinkType(CMakeLinkType.SHARED);
    return this;
  }

  public CMakeExecutableDependencies linkInterface() {
    setLinkType(CMakeLinkType.INTERFACE);
    return this;
  }

  public CMakeExecutableDependencies setPrivate() {
    setVisibilityPrivate(true);
    return this;
  }

  public CMakeExecutableDependencies setPublic() {
    setVisibilityPrivate(false);
    return this;
  }

}
