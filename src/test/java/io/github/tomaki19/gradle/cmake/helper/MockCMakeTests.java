/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTests;

public class MockCMakeTests implements CMakeTests {

  @Override
  public CMakeCompile getPrivateCompile() {
    return mock(CMakeCompile.class);
  }

  @Override
  public CMakeExecutableLinking getPrivateLinking() {
    return mock(CMakeExecutableLinking.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return mock(Property.class);
  }

  @Override
  public Property<Boolean> getTestResultsXmlOutput() {
    return mock(Property.class);
  }

}
