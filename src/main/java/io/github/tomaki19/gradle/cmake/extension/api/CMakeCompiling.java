/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

abstract class CMakeCompiling {

  public static final CMakeVisibility PUBLIC = CMakeVisibility.PUBLIC;
  public static final CMakeVisibility PRIVATE = CMakeVisibility.PRIVATE;

  private final Collection<CMakeBuildSpec> defines = new HashSet<>();
  private final Collection<CMakeBuildSpec> options = new HashSet<>();

  public Collection<CMakeBuildSpec> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public void options(final Collection<CharSequence> names, final Map<String, Object> spec) {
    options.add(CMakeBuildSpec.Init.create(names, spec));
  }

  public void option(final CharSequence name, final Map<String, Object> spec) {
    options(Arrays.asList(name), spec);
  }

  public Collection<CMakeBuildSpec> getDefines() {
    return Collections.unmodifiableCollection(defines);
  }

  public void defines(final Collection<CharSequence> names, final Map<String, Object> spec) {
    defines.add(CMakeBuildSpec.Init.create(names, spec));
  }

  public void define(final CharSequence name, final Map<String, Object> spec) {
    defines(Arrays.asList(name), spec);
  }

}
