/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeExecutableDependencies extends CMakeBinaryDependencies {

  public CMakeExecutableDependencies(final CharSequence... names) {
    super(CMakeLinkVariant.SHARED, CMakeVisibilityType.PRIVATE, names);
  }

  public CMakeExecutableDependencies from(final CharSequence value) {
    setFrom(value.toString());
    return this;
  }

  public CMakeExecutableDependencies link(final CMakeLinkVariant variant) {
    setLinkVariant(variant);
    return this;
  }

  public CMakeExecutableDependencies visibility(final CMakeVisibilityType variant) {
    setVisibilityType(variant);
    return this;
  }

}
