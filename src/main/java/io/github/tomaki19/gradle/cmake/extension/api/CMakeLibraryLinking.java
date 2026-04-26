/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeLibraryLinking extends CMakeLinking {

  final Collection<CMakeLibraryDependencies> dependencies = new HashSet<>();

  public CMakeLibraryLinking() {
    super(CMakeVisibility.PUBLIC);
  }

  public Collection<CMakeLibraryDependencies> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public CMakeLibraryDependencies link(final CharSequence... names) {
    final CMakeLibraryDependencies entry = new CMakeLibraryDependencies(names);
    dependencies.add(entry);
    return entry;
  }

  public void link(final Collection<CMakeLibraryDependencies> entries) {
    dependencies.addAll(entries);
  }

}

/*
 * linking {
 * link(['libA', 'libB'],
 * from: 'projectA'
 * build: Shared
 * link: Shared
 * visibility: Private
 * )
 * }
 *
 */
