/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildType;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;

public class MockCMakeLibrary extends CMakeLibrary {

  private final String name;
  private final Property<String> outputName;
  private final Property<Boolean> stripDebug;
  private final SetProperty<CMakeBuildType> libraryTypes;

  public MockCMakeLibrary(final String name, final ObjectFactory factory) {
    super(factory);
    this.name = name;
    this.outputName = factory.property(String.class);
    this.stripDebug = factory.property(Boolean.class);
    this.libraryTypes = factory.setProperty(CMakeBuildType.class);
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
  public CMakeCompile getPublicCompile() {
    return mock(CMakeCompile.class);
  }

  @Override
  public CMakeLibraryLinking getPrivateLinking() {
    return mock(CMakeLibraryLinking.class);
  }

  @Override
  public CMakeLibraryLinking getPublicLinking() {
    return mock(CMakeLibraryLinking.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return stripDebug;
  }

  @Override
  public SetProperty<CMakeBuildType> getBuildTypes() {
    return libraryTypes;
  }

}
