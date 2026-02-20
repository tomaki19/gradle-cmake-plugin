/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeToolchain extends CMakeNamedObject {

  public static final OperatingSystem Linux = OperatingSystem.LINUX;
  public static final OperatingSystem MacOS = OperatingSystem.MAC_OS;
  public static final OperatingSystem Windows = OperatingSystem.WINDOWS;

  private final CMakeBuildConfigs buildConfigs = new CMakeBuildConfigs();

  public abstract Property<OperatingSystem> getOperatingSystem();

  public abstract Property<String> getGenerator();

  public Set<String> getBuildConfigs() {
    return buildConfigs.get();
  }

  public void buildConfigs(final String... values) {
    buildConfigs.set(values);
  }

  public abstract MapProperty<String, String> getEnvironment();

  public abstract RegularFileProperty getEnvironmentFile();

  public abstract RegularFileProperty getToolchainFile();

  @Nested
  public abstract CMakeLibraries getLibraries();

  public void libraries(Action<CMakeLibraries> action) {
    action.execute(getLibraries());
  }

  @Nested
  public abstract CMakeApplications getApplications();

  public void applications(Action<CMakeApplications> action) {
    action.execute(getApplications());
  }

  @Nested
  public abstract CMakeTests getTests();

  public void tests(Action<CMakeTests> action) {
    action.execute(getTests());
  }

}
