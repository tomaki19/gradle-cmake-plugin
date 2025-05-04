/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.internal.os.OperatingSystem;

public interface CMakeToolchain extends CMakeNamedObject {

  static final OperatingSystem Linux = OperatingSystem.LINUX;
  static final OperatingSystem MacOs = OperatingSystem.MAC_OS;
  static final OperatingSystem Windows = OperatingSystem.WINDOWS;

  static final Collection<String> BuildConfigDefaults = Arrays.asList("release", "debug");

  Property<OperatingSystem> getOperatingSystem();

  Property<String> getCompiler();

  Property<String> getArchitecture();

  Property<String> getGenerator();

  SetProperty<String> getBuildConfigs();

  MapProperty<String, String> getEnvironment();

  Property<File> getEnvironmentFile();

  RegularFileProperty getToolchainFile();

  SetProperty<String> getLibraryLinkDependencies();

  SetProperty<String> getApplicationLinkDependencies();

  SetProperty<String> getTestLinkDependencies();

  Property<Boolean> getBuildStatic();

  Property<Boolean> getBuildShared();

  Property<Boolean> getStripDebug();

  Property<Boolean> getPackageBuildOutputs();

}
