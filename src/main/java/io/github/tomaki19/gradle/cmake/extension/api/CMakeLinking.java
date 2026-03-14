/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

abstract class CMakeLinking {

  public static final CMakeLinkVariant Static = CMakeLinkVariant.STATIC;
  public static final CMakeLinkVariant Shared = CMakeLinkVariant.SHARED;
  public static final CMakeLinkVariant Interface = CMakeLinkVariant.INTERFACE;

  public static final CMakeVisibilityType Public = CMakeVisibilityType.PUBLIC;
  public static final CMakeVisibilityType Private = CMakeVisibilityType.PRIVATE;

  private final Collection<CMakeBuildItems> options = new HashSet<>();
  private CMakeVisibilityType defaultVisibilityType;

  CMakeLinking(final CMakeVisibilityType defaultVisibilityType) {
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
