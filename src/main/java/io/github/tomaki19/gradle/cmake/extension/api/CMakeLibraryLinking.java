/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class CMakeLibraryLinking extends CMakeLinking {

  final Collection<CMakeLibraryLinkSpec> dependencySpecs = new HashSet<>();

  public Collection<CMakeLibraryLinkSpec> getDependencySpecs() {
    return Collections.unmodifiableCollection(dependencySpecs);
  }

  public void link(final Map<String, Object> entries, final CharSequence... components) {
    dependencySpecs.add(CMakeLibraryLinkSpec.Init.create(entries, components));
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
