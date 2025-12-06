/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Nested;

public abstract class CMakeLibrary extends CMakeBinary implements CMakeLibraries {

  @Inject
  public CMakeLibrary(ObjectFactory objectFactory) {
    super(objectFactory);
  }

  @Nested
  public abstract CMakeCompile getPublicCompile();

  public void publicCompile(Action<? super CMakeCompile> action) {
    action.execute(getPublicCompile());
  }

  @Nested
  public abstract CMakeLinking getPublicInterfaceLinking();

  public void publicInterfaceLinking(Action<? super CMakeLinking> action) {
    action.execute(getPublicInterfaceLinking());
  }

  @Nested
  public abstract CMakeLinking getPublicStaticLinking();

  public void publicStaticLinking(Action<? super CMakeLinking> action) {
    action.execute(getPublicStaticLinking());
  }

  @Nested
  public abstract CMakeLinking getPublicSharedLinking();

  public void publicSharedLinking(Action<? super CMakeLinking> action) {
    action.execute(getPublicSharedLinking());
  }
}
