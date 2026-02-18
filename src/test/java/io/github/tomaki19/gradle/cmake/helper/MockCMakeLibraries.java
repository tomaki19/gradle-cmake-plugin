/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import static org.mockito.Mockito.mock;

import org.gradle.api.provider.Property;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraries;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;

public class MockCMakeLibraries implements CMakeLibraries {

  @Override
  public CMakeCompile getPrivateCompile() {
    return mock(CMakeCompile.class);
  }

  @Override
  public CMakeLibraryLinking getPrivateLinking() {
    return mock(CMakeLibraryLinking.class);
  }

  @Override
  public Property<Boolean> getBuildStatic() {
    return mock(Property.class);
  }

  @Override
  public Property<Boolean> getBuildShared() {
    return mock(Property.class);
  }

  @Override
  public Property<Boolean> getStripDebug() {
    return mock(Property.class);
  }
}
