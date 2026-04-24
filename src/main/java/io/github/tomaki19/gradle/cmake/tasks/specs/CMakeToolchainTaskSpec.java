/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import java.util.Set;
import java.util.TreeSet;

public class CMakeToolchainTaskSpec {

  public static final String ALL = "*";

  public final Set<String> toolchains = new TreeSet<>();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((toolchains == null) ? 0 : toolchains.hashCode());
    return result;
  }

  public boolean equals(final CMakeToolchainTaskSpec other) {
    if (this == other)
      return true;
    if (!toolchains.equals(other.toolchains))
      return false;
    return true;
  }

  public boolean equals(final CMakeToolchainMatch match) {
    return toolchains.contains(match.getToolchain().getName()) || toolchains.contains(ALL);
  }

}

/*
 *
 * cmake.tasks.register(
 * toolchains: ['gcc', 'vscp'],
 * ) {
 * }
 *
 */
