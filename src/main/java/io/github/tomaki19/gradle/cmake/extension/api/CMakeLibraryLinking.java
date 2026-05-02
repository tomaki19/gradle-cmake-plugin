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

public class CMakeLibraryLinking extends CMakeLinking {

  final Collection<CMakeLibraryLinkSpec> dependencySpecs = new HashSet<>();

  public Collection<CMakeLibraryLinkSpec> getDependencySpecs() {
    return Collections.unmodifiableCollection(dependencySpecs);
  }

  public void link(final Collection<CharSequence> components, final Map<String, Object> entries) {
    dependencySpecs.add(CMakeLibraryLinkSpec.Init.create(components, entries));
  }

  public void link(final CharSequence component, final Map<String, Object> entries) {
    link(Arrays.asList(component), entries);
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
