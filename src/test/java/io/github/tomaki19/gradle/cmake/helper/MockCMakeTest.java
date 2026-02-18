/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public class MockCMakeTest extends CMakeTest {

  private final String name;

  public MockCMakeTest(final String name, final ObjectFactory factory) {
    super(factory);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

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
