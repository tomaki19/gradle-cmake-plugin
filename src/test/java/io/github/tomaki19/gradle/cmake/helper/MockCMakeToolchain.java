/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplications;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraries;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTests;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public class MockCMakeToolchain extends CMakeToolchain {

  private final String name;

  public MockCMakeToolchain(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Property<String> getGenerator() {
    return mock(Property.class);
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
