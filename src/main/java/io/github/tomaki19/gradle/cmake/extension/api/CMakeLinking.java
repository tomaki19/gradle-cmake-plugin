/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

abstract class CMakeLinking {

  public static final CMakeLinkVariant STATIC = CMakeLinkVariant.STATIC;
  public static final CMakeLinkVariant SHARED = CMakeLinkVariant.SHARED;
  public static final CMakeLinkVariant INTERFACE = CMakeLinkVariant.INTERFACE;

  public static final CMakeVisibility PUBLIC = CMakeVisibility.PUBLIC;
  public static final CMakeVisibility PRIVATE = CMakeVisibility.PRIVATE;

  private final Collection<CMakeBuildItems> options = new HashSet<>();
  private CMakeVisibility defaultVisibilityType;

  CMakeLinking(final CMakeVisibility defaultVisibilityType) {
    this.defaultVisibilityType = defaultVisibilityType;
  }

  public Collection<CMakeBuildItems> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public CMakeBuildItems options(final CharSequence... values) {
    final CMakeBuildItems entry = new CMakeBuildItems(defaultVisibilityType, values);
    options.add(entry);
    return entry;
  }

  public void options(final Collection<CMakeBuildItems> entries) {
    options.addAll(entries);
  }

}
