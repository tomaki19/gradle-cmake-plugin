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

public class CMakeExecutableLinking extends CMakeLinking {

  final Collection<CMakeExecutableLinkSpec> dependencySpecs = new HashSet<>();

  public Collection<CMakeExecutableLinkSpec> getDependencySpecs() {
    return Collections.unmodifiableCollection(dependencySpecs);
  }

  public void link(final Collection<CharSequence> components, final Map<String, Object> spec) {
    dependencySpecs.add(CMakeExecutableLinkSpec.Init.create(components, spec));
  }

  public void link(final CharSequence component, final Map<String, Object> spec) {
    link(Arrays.asList(component), spec);
  }

}
