/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class CMakeExecutableLinking extends CMakeLinking {

  final Collection<CMakeExecutableLinkSpec> dependencySpecs = new HashSet<>();

  public Collection<CMakeExecutableLinkSpec> getDependencySpecs() {
    return Collections.unmodifiableCollection(dependencySpecs);
  }

  public void link(final Map<String, Object> spec, final CharSequence... components) {
    dependencySpecs.add(CMakeExecutableLinkSpec.Init.create(spec, components));
  }

}
