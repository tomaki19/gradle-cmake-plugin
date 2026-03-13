/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeLibraryLinking extends CMakeLinking {

  final Collection<CMakeLibraryDependencies> dependencies = new HashSet<>();

  public CMakeLibraryLinking() {
    super(CMakeVisibilityType.PUBLIC);
  }

  public Collection<CMakeLibraryDependencies> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public CMakeLibraryDependencies dependencies(final CharSequence... names) {
    final CMakeLibraryDependencies entry = new CMakeLibraryDependencies(names);
    dependencies.add(entry);
    return entry;
  }

  public void dependencies(final Collection<CMakeLibraryDependencies> entries) {
    dependencies.addAll(entries);
  }

}
