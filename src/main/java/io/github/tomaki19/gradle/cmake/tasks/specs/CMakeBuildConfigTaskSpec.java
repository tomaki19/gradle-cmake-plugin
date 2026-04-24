/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks.specs;

import java.util.Set;
import java.util.TreeSet;

public class CMakeBuildConfigTaskSpec extends CMakeToolchainTaskSpec {

  public final Set<String> buildConfigs = new TreeSet<>();

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((buildConfigs == null) ? 0 : buildConfigs.hashCode());
    return result;
  }

  public boolean equals(CMakeBuildConfigTaskSpec other) {
    if (this == other)
      return true;
    if (!buildConfigs.equals(other.buildConfigs))
      return false;
    return true;
  }

  public boolean equals(final CMakeBuildConfigMatch match) {
    return super.equals(match)
        && (buildConfigs.contains(match.getBuildConfig()) || buildConfigs.contains(ALL));
  }
}

/*
 *
 * cmake.tasks.register(
 * toolchains: ['gcc', 'vscp'],
 * buildConfigs: ['release', 'debug'],
 * ) {
 * }
 *
 */
