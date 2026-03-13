/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeExecutableLinking extends CMakeLinking {

  final Collection<CMakeExecutableDependencies> dependencies = new HashSet<>();

  public CMakeExecutableLinking() {
    super(CMakeVisibilityType.PRIVATE);
  }

  public Collection<CMakeExecutableDependencies> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public CMakeExecutableDependencies dependency(final CharSequence name) {
    final CMakeExecutableDependencies entry = new CMakeExecutableDependencies(name);
    dependencies.add(entry);
    return entry;
  }

  public void dependency(final CMakeExecutableDependencies entry) {
    dependencies.add(entry);
  }

  public CMakeExecutableDependencies dependencies(final CharSequence... names) {
    final CMakeExecutableDependencies entry = new CMakeExecutableDependencies(names);
    dependencies.add(entry);
    return entry;
  }

  public void dependencies(final Collection<CMakeExecutableDependencies> entries) {
    dependencies.addAll(entries);
  }

}
