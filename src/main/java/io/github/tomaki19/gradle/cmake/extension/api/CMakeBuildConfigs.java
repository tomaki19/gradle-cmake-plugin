/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CMakeBuildConfigs {

  private Set<String> entries = new HashSet<>(Arrays.asList("debug", "release"));

  public void set(final String... entries) {
    this.entries = new HashSet<>(Arrays.asList(entries));
  }

  public Set<String> get() {
    return Collections.unmodifiableSet(entries);
  }

}
