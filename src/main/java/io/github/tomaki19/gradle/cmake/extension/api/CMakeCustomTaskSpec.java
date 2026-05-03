/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Objects;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeCustomTaskSpec<T> {

  public static final String ALL = "*";
  public static final String LIBRARIES = "*library";
  public static final String INTERFACES = "*interface";
  public static final String SHARED = "*shared";
  public static final String STATIC = "*static";
  public static final String EXECUTABLES = "*executable";
  public static final String APPLICATIONS = "*application";
  public static final String TESTS = "*test";

  protected static final String TOOLCHAINS = "toolchains";
  protected static final String BUILD_CONFIGS = "buildConfigs";
  protected static final String COMPONENTS = "components";

  private final Set<String> toolchains;
  private final Set<String> buildConfigs;
  private final Set<String> components;

  public CMakeCustomTaskSpec(final Set<String> toolchains, final Set<String> buildConfigs,
      final Set<String> components) {
    this.toolchains = Collections.unmodifiableSet(toolchains);
    this.buildConfigs = Collections.unmodifiableSet(buildConfigs);
    this.components = Collections.unmodifiableSet(components);
  }

  private Set<String> getToolchains() {
    return toolchains;
  }

  private Set<String> getBuildConfigs() {
    return buildConfigs;
  }

  private Set<String> getComponents() {
    return components;
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
    result = prime * result + getToolchains().hashCode();
    result = prime * result + getBuildConfigs().hashCode();
    result = prime * result + getComponents().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeCustomTaskSpec))
      return false;
    final CMakeCustomTaskSpec<?> other = (CMakeCustomTaskSpec<?>) obj;
    if (!getToolchains().equals(other.getToolchains()))
      return false;
    if (!getBuildConfigs().equals(other.getBuildConfigs()))
      return false;
    if (!getComponents().equals(other.getComponents()))
      return false;
    return true;
  }

  static class Init extends CMakeApiSpecInit {

    protected static void validateContentTypes(final Map<String, Object> entries) throws CMakeApiException {
      validateType(entries.get(TOOLCHAINS), TOOLCHAINS, Collection.class);
      validateType(entries.get(BUILD_CONFIGS), BUILD_CONFIGS, Collection.class);
      validateType(entries.get(COMPONENTS), COMPONENTS, Collection.class);
    }

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
