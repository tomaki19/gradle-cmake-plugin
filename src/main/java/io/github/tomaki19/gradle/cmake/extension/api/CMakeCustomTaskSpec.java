/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Objects;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeCustomTaskSpec {

  private static final CharSequence ALL = "*";
  private static final CharSequence LIBRARIES = "*library";
  private static final CharSequence INTERFACES = "*interface";
  private static final CharSequence SHARED = "*shared";
  private static final CharSequence STATIC = "*static";
  private static final CharSequence EXECUTABLES = "*executable";
  private static final CharSequence APPLICATIONS = "*application";
  private static final CharSequence TESTS = "*test";

  private static final String TOOLCHAINS = "toolchains";
  private static final String BUILD_CONFIGS = "buildConfigs";
  private static final String COMPONENTS = "components";

  private final Map<String, Set<CharSequence>> spec = new HashMap<>();

  public CMakeCustomTaskSpec(final Map<String, List<CharSequence>> entries) {
    entries.forEach((key, value) -> spec.put(key, new HashSet<>(value)));
  }

  private Set<CharSequence> getToolchains() {
    return spec.getOrDefault(TOOLCHAINS, new TreeSet<>());
  }

  private Set<CharSequence> getBuildConfigs() {
    return spec.getOrDefault(BUILD_CONFIGS, new TreeSet<>());
  }

  private Set<CharSequence> getComponents() {
    return spec.getOrDefault(COMPONENTS, new TreeSet<>());
  }

  public boolean hasNoToolchains() {
    return getToolchains().isEmpty();
  }

  public boolean hasNoBuildConfigs() {
    return getBuildConfigs().isEmpty();
  }

  public boolean hasNoComponents() {
    return getComponents().isEmpty();
  }

  public boolean matchesToolchain(final CMakeResolvedToolchain toolchain) {
    return getToolchains().contains(ALL) || getToolchains().contains(toolchain.getName());
  }

  public boolean matchesBuildConfig(final String buildConfig) {
    return getBuildConfigs().contains(ALL) || getBuildConfigs().contains(buildConfig);
  }

  public boolean matchesLibrary(final CMakeResolvedLibrary library) {
    return getComponents().contains(ALL)
        || getComponents().contains(LIBRARIES)
        || (getComponents().contains(INTERFACES)
            && Objects.equals(CMakeLinkVariant.INTERFACE, library.getLinkVariant()))
        || (getComponents().contains(SHARED) && Objects.equals(CMakeLinkVariant.SHARED, library.getLinkVariant()))
        || (getComponents().contains(STATIC) && Objects.equals(CMakeLinkVariant.STATIC, library.getLinkVariant()))
        || getComponents().contains(library.getName());
  }

  public boolean matchesApplication(final CMakeResolvedApplication application) {
    return getComponents().contains(ALL)
        || getComponents().contains(EXECUTABLES)
        || getComponents().contains(APPLICATIONS)
        || getComponents().contains(application.getName());
  }

  public boolean matchesTest(final CMakeResolvedTest test) {
    return getComponents().contains(ALL)
        || getComponents().contains(EXECUTABLES)
        || getComponents().contains(TESTS)
        || getComponents().contains(test.getName());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getToolchains() == null) ? 0 : getToolchains().hashCode());
    result = prime * result + ((getBuildConfigs() == null) ? 0 : getBuildConfigs().hashCode());
    result = prime * result + ((getComponents() == null) ? 0 : getComponents().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeCustomTaskSpec))
      return false;
    CMakeCustomTaskSpec other = (CMakeCustomTaskSpec) obj;
    if (getToolchains() == null) {
      if (other.getToolchains() != null)
        return false;
    } else if (!getToolchains().equals(other.getToolchains()))
      return false;
    if (getBuildConfigs() == null) {
      if (other.getBuildConfigs() != null)
        return false;
    } else if (!getBuildConfigs().equals(other.getBuildConfigs()))
      return false;
    if (getComponents() == null) {
      if (other.getComponents() != null)
        return false;
    } else if (!getComponents().equals(other.getComponents()))
      return false;
    return true;
  }

}

/*
 * cmake.tasks.register(<type>,
 * toolchains: ['gcc', 'vscp'],
 * buildConfigs: ['release', 'debug'],
 * components: ["*", "*library", "*interface", "*shared", "*static",
 * "*executable", "*application", "*test"],
 * ) {
 * ...
 * }
 */
