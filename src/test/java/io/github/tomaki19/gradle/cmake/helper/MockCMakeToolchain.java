/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;



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
  private final CMakeLibraries libraries;
  private final CMakeApplications applications;
  private final CMakeTests tests;

  public MockCMakeToolchain(final String name, final ObjectFactory factory) {
    this.name = name;
    this.operatingSystem = factory.property(OperatingSystem.class);
    this.generator = factory.property(String.class);
    this.environment = factory.mapProperty(String.class, String.class);
    this.environmentFile = factory.fileProperty();
    this.toolchainFile = factory.fileProperty();
    this.libraries = new MockCMakeLibraries(factory);
    this.applications = new MockCMakeApplications(factory);
    this.tests = new MockCMakeTests(factory);
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
    return libraries;
  }

  @Override
  public CMakeApplications getApplications() {
    return applications;
  }

  @Override
  public CMakeTests getTests() {
    return tests;
  }
}
