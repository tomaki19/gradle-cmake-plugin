/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplications;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableCompiling;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;

public class MockCMakeApplications implements CMakeApplications {

  private final Property<Boolean> stripDebug;

  public MockCMakeApplications(final ObjectFactory factory) {
    this.stripDebug = factory.property(Boolean.class);
  }

  @Override
  public CMakeExecutableCompiling getCompiling() {
    return mock(CMakeExecutableCompiling.class);
  }

  @Override
  public CMakeExecutableLinking getLinking() {
    return mock(CMakeExecutableLinking.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return stripDebug;
  }

}
