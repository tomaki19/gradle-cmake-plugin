/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class CMakeBuildConfigs {

  private Set<String> entries = new TreeSet<>(Arrays.asList("Debug", "Release", "RelWithDebInfo", "MinSizeRel"));

  public void set(final String... entries) {
    this.entries = new TreeSet<>(Arrays.asList(entries));
  }

  public Set<String> get() {
    return Collections.unmodifiableSet(entries);
  }

}
