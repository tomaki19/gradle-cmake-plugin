/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryCompiling;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;

public class MockCMakeLibrary extends CMakeLibrary {

  private final String name;
  private final Property<String> outputName;
  private final Property<String> outputVersion;
  private final Property<Boolean> stripDebug;
  private final SetProperty<CMakeBuildVariant> libraryTypes;

  public MockCMakeLibrary(final String name, final ObjectFactory factory) {
    super(factory);
    this.name = name;
    this.outputName = factory.property(String.class);
    this.outputVersion = factory.property(String.class);
    this.stripDebug = factory.property(Boolean.class);
    this.libraryTypes = factory.setProperty(CMakeBuildVariant.class);
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
  public Property<String> getOutputVersion() {
    return outputVersion;
  }

  @Override
  public CMakeLibraryCompiling getCompiling() {
    return mock(CMakeLibraryCompiling.class);
  }

  @Override
  public CMakeLibraryLinking getLinking() {
    return mock(CMakeLibraryLinking.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return stripDebug;
  }

  @Override
  public SetProperty<CMakeBuildVariant> getBuildVariants() {
    return libraryTypes;
  }

}
