/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeExecutableLinking extends CMakeLinking {

  final Collection<CMakeExecutableDependencies> dependencies = new HashSet<>();

  public CMakeExecutableLinking() {
    super(CMakeVisibility.PRIVATE);
  }

  public Collection<CMakeExecutableDependencies> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public CMakeExecutableDependencies link(final CharSequence... names) {
    final CMakeExecutableDependencies entry = new CMakeExecutableDependencies(names);
    dependencies.add(entry);
    return entry;
  }

  public void link(final Collection<CMakeExecutableDependencies> entries) {
    dependencies.addAll(entries);
  }

}
