/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;

public class MockCMakeApplication extends CMakeApplication {

  private final String name;
  private final Property<String> outputName;
  private final Property<Boolean> stripDebug;

  public MockCMakeApplication(final String name, final ObjectFactory factory) {
    super(factory);
    this.name = name;
    this.outputName = factory.property(String.class);
    this.stripDebug = factory.property(Boolean.class);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Property<String> getOutputName() {
    return outputName;
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
    return stripDebug;
  }

}
