/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;


import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplications;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraries;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTests;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public class MockCMakeToolchain extends CMakeToolchain {

  private final String name;
  private final Property<OperatingSystem> operatingSystem;
  private final Property<String> generator;
  private final MapProperty<String, String> environment;
  private final RegularFileProperty environmentFile;
  private final RegularFileProperty toolchainFile;

  public MockCMakeToolchain(final String name, final ObjectFactory factory) {
    this.name = name;
    this.operatingSystem = factory.property(OperatingSystem.class);
    this.generator = factory.property(String.class);
    this.environment = factory.mapProperty(String.class, String.class);
    this.environmentFile = factory.fileProperty();
    toolchainFile = factory.fileProperty();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Property<OperatingSystem> getOperatingSystem() {
    return operatingSystem;
  }

  @Override
  public Property<String> getGenerator() {
    return generator;
  }

  @Override
  public MapProperty<String, String> getEnvironment() {
    return environment;
  }

  @Override
  public RegularFileProperty getEnvironmentFile() {
    return environmentFile;
  }

  @Override
  public RegularFileProperty getToolchainFile() {
    return toolchainFile;
  }

  @Override
  public CMakeLibraries getLibraries() {
    return mock(CMakeLibraries.class);
  }

  @Override
  public CMakeApplications getApplications() {
    return mock(CMakeApplications.class);
  }

  @Override
  public CMakeTests getTests() {
    return mock(CMakeTests.class);
  }
}
