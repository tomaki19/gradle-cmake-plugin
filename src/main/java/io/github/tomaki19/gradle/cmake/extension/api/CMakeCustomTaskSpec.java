/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Objects;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedApplication;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedTest;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public abstract class CMakeCustomTaskSpec<T> {

  private static final String TOOLCHAINS = "toolchains";
  private static final String BUILD_CONFIGS = "buildConfigs";
  private static final String COMPONENTS = "components";

  private static final String ALL = "*";
  private static final String LIBRARIES = "*library";
  private static final String INTERFACES = "*interface";
  private static final String SHARED = "*shared";
  private static final String STATIC = "*static";
  private static final String EXECUTABLES = "*executable";
  private static final String APPLICATIONS = "*application";
  private static final String TESTS = "*test";

  protected final Map<String, Object> spec;

  public CMakeCustomTaskSpec(final Map<String, Object> entries) {
    this.spec = Collections.unmodifiableMap(entries);
  }

  public void validateContentTypes() throws IllegalArgumentException {
    validateType(TOOLCHAINS, Collection.class);
    validateType(BUILD_CONFIGS, Collection.class);
    validateType(COMPONENTS, Collection.class);
  }

  protected void validateType(final String name, final Class<?> type) throws IllegalArgumentException {
    if (spec.containsKey(name) && !(type.isAssignableFrom(spec.get(name).getClass()))) {
      throw new IllegalArgumentException("Invalid %s of type %s!".formatted(name, spec.get(name).getClass()));
    }
  }

  protected void validateMandatory(final String name) throws IllegalArgumentException {
    if (!spec.containsKey(name)) {
      throw new IllegalArgumentException("Missing mandatory %s!".formatted(name));
    }
  }

  @SuppressWarnings("unchecked")
  private Set<String> getToolchains() {
    return new HashSet<>((Collection<String>) spec.getOrDefault(TOOLCHAINS, Collections.emptyList()));
  }

  @SuppressWarnings("unchecked")
  private Set<String> getBuildConfigs() {
    return new HashSet<>((Collection<String>) spec.getOrDefault(BUILD_CONFIGS, Collections.emptyList()));
  }

  @SuppressWarnings("unchecked")
  private Set<String> getComponents() {
    return new HashSet<>((Collection<String>) spec.getOrDefault(COMPONENTS, Collections.emptyList()));
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
