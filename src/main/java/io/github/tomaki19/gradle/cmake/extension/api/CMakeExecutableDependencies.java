/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

public class CMakeExecutableDependencies extends CMakeBinaryDependencies {

  public CMakeExecutableDependencies(final CharSequence... names) {
    super(names);
  }

  public CMakeExecutableDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeExecutableDependencies getLinkStatic() {
    setLinkage(CMakeLinkType.STATIC);
    return this;
  }

  public CMakeExecutableDependencies getLinkShared() {
    setLinkage(CMakeLinkType.SHARED);
    return this;
  }

  public CMakeExecutableDependencies getLinkInterface() {
    setLinkage(CMakeLinkType.INTERFACE);
    return this;
  }

}
