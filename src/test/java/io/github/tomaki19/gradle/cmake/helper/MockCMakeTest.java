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
  private final Property<String> outputName;
  private final Property<Boolean> stripDebug;
  private final Property<Boolean> testResultsXmlOutput;

  public MockCMakeTest(final String name, final ObjectFactory factory) {
    super(factory);
    this.outputName = factory.property(String.class);
    this.name = name;
    this.stripDebug = factory.property(Boolean.class);
    this.testResultsXmlOutput = factory.property(Boolean.class);
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

  @Override
  public Property<Boolean> getTestResultsXmlOutput() {
    return testResultsXmlOutput;
  }

}
