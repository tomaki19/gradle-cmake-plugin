/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.tasks.Nested;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeToolchain implements Named, Comparable<CMakeToolchain> {

  public static final OperatingSystem Linux = OperatingSystem.LINUX;
  public static final OperatingSystem MacOS = OperatingSystem.MAC_OS;
  public static final OperatingSystem Windows = OperatingSystem.WINDOWS;

  private static final OperatingSystem DEFAULT_OPERATING_SYSTEM = OperatingSystem.current();
  private static final Collection<String> DEFAULT_BUILD_CONFIGS = Arrays.asList("debug", "release");

  private Optional<OperatingSystem> operatingSystem = Optional.of(DEFAULT_OPERATING_SYSTEM);
  private Optional<String> generator = Optional.empty();
  private Collection<String> buildConfigs = new HashSet<>(DEFAULT_BUILD_CONFIGS);
  private Map<String, String> environment = new HashMap<>();
  private Optional<File> environmentFile = Optional.empty();
  private Optional<File> toolchainFile = Optional.empty();

  public Optional<OperatingSystem> getOperatingSystem() {
    return operatingSystem;
  }

  public void setOperatingSystem(final OperatingSystem value) {
    this.operatingSystem = Optional.of(value);
  }

  public Optional<String> getGenerator() {
    return generator;
  }

  public void setGenerator(final CharSequence value) {
    this.generator = Optional.of(value.toString());
  }

  public Collection<String> getBuildConfigs() {
    return buildConfigs;
  }

  public void setBuildConfigs(final Collection<CharSequence> values) {
    this.buildConfigs = values.stream().map((value) -> value.toString()).toList();
  }

  public void buildConfigs(final CharSequence... values) {
    setBuildConfigs(Arrays.asList(values));
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public void setEnvironment(final Map<CharSequence, CharSequence> values) {
    this.environment = values.entrySet().stream().collect(
        Collectors.toUnmodifiableMap((entry) -> entry.getKey().toString(), (entry) -> entry.getValue().toString()));
  }

  public Optional<File> getEnvironmentFile() {
    return environmentFile;
  }

  public void setEnvironmentFile(final File value) {
    this.environmentFile = Optional.of(value);
  }

  public Optional<File> getToolchainFile() {
    return toolchainFile;
  }

  public void setToolchainFile(final File value) {
    this.toolchainFile = Optional.of(value);
  }

  @Nested
  public abstract CMakeBinaries getBinaries();

  public void binaries(Action<? super CMakeBinaries> action) {
    action.execute(getBinaries());
  }

  @Nested
  public abstract CMakeLibraries getLibraries();

  public void libraries(Action<? super CMakeLibraries> action) {
    action.execute(getLibraries());
  }

  @Nested
  public abstract CMakeApplications getApplications();

  public void applications(Action<? super CMakeApplications> action) {
    action.execute(getApplications());
  }

  @Nested
  public abstract CMakeTests getTests();

  public void tests(Action<? super CMakeTests> action) {
    action.execute(getTests());
  }

  @Override
  public int compareTo(CMakeToolchain other) {
    int comparator = 0;
    if ((comparator = getName().compareTo(other.getName())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
