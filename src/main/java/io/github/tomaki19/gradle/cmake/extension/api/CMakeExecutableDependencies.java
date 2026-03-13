/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeExecutableDependencies extends CMakeBinaryDependencies {

  public CMakeExecutableDependencies(final CharSequence... names) {
    super(CMakeLinkType.SHARED, CMakeVisibilityType.PRIVATE, names);
  }

  public CMakeExecutableDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeExecutableDependencies link(final CMakeLinkType type) {
    setLinkType(type);
    return this;
  }

  public CMakeExecutableDependencies visibility(final CMakeVisibilityType type) {
    setVisibilityType(type);
    return this;
  }

}
