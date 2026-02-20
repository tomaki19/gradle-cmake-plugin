/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraries;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;

public class MockCMakeLibraries implements CMakeLibraries {

  private final Property<Boolean> stripDebug;
  private final Property<Boolean> buildStatic;
  private final Property<Boolean> buildShared;

  public MockCMakeLibraries(final ObjectFactory factory) {
    this.stripDebug = factory.property(Boolean.class);
    this.buildStatic = factory.property(Boolean.class);
    this.buildShared = factory.property(Boolean.class);
  }

  @Override
  public CMakeCompile getPrivateCompile() {
    return mock(CMakeCompile.class);
  }

  @Override
  public CMakeLibraryLinking getPrivateLinking() {
    return mock(CMakeLibraryLinking.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return stripDebug;
  }

  @Override
  public Property<Boolean> getBuildStatic() {
    return buildStatic;
  }

  @Override
  public Property<Boolean> getBuildShared() {
    return buildShared;
  }
}
