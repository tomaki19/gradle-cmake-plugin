/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.gradle.api.Action;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.internal.os.OperatingSystem;

public abstract class CMakeToolchain implements CMakeNamedObject {

  public static final OperatingSystem Linux = OperatingSystem.LINUX;
  public static final OperatingSystem MacOs = OperatingSystem.MAC_OS;
  public static final OperatingSystem Windows = OperatingSystem.WINDOWS;

  public static final Collection<String> BuildConfigDefaults = Arrays.asList("release", "debug");

  public abstract Property<OperatingSystem> getOperatingSystem();

  public abstract Property<String> getCompiler();

  public abstract Property<String> getArchitecture();

  public abstract Property<String> getGenerator();

  public abstract SetProperty<String> getBuildConfigs();

  public abstract MapProperty<String, String> getEnvironment();

  public abstract Property<File> getEnvironmentFile();

  public abstract Property<File> getToolchainFile();

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
}
