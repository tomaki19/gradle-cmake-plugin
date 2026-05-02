/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

abstract class CMakeLinking {

  public static final CMakeLinkVariant STATIC = CMakeLinkVariant.STATIC;
  public static final CMakeLinkVariant SHARED = CMakeLinkVariant.SHARED;
  public static final CMakeLinkVariant INTERFACE = CMakeLinkVariant.INTERFACE;

  public static final CMakeVisibility PUBLIC = CMakeVisibility.PUBLIC;
  public static final CMakeVisibility PRIVATE = CMakeVisibility.PRIVATE;

  private final Collection<CMakeBuildSpec> options = new HashSet<>();

  public Collection<CMakeBuildSpec> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public void options(final Map<String, Object> spec, final CharSequence... names) {
    options.add(CMakeBuildSpec.Init.create(spec, names));
  }

}
