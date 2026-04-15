/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

abstract class CMakeCompiling {

  public static final CMakeVisibility Public = CMakeVisibility.PUBLIC;
  public static final CMakeVisibility Private = CMakeVisibility.PRIVATE;

  private final Collection<CMakeBuildItems> defines = new HashSet<>();
  private final Collection<CMakeBuildItems> options = new HashSet<>();
  private CMakeVisibility defaultVisibilityType;

  CMakeCompiling(final CMakeVisibility defaultVisibilityType) {
    this.defaultVisibilityType = defaultVisibilityType;
  }

  public Collection<CMakeBuildItems> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public CMakeBuildItems options(CharSequence... values) {
    final CMakeBuildItems entry = new CMakeBuildItems(defaultVisibilityType, values);
    options.add(entry);
    return entry;
  }

  public Collection<CMakeBuildItems> getDefines() {
    return Collections.unmodifiableCollection(defines);
  }

  public CMakeBuildItems defines(CharSequence... values) {
    final CMakeBuildItems entry = new CMakeBuildItems(defaultVisibilityType, values);
    defines.add(entry);
    return entry;
  }

}
