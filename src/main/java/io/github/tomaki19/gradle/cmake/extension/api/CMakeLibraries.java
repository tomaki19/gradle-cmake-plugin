/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface CMakeLibraries extends CMakeBinaries {

  public abstract Property<Boolean> getBuildStatic();

  public abstract Property<Boolean> getBuildShared();

  @Nested
  public abstract CMakeLinking getPrivateInterfaceLinking();

  public default void privateInterfaceLinking(Action<? super CMakeLinking> action) {
    action.execute(getPrivateInterfaceLinking());
  }

  @Nested
  public abstract CMakeLinking getPrivateStaticLinking();

  public default void privateStaticLinking(Action<? super CMakeLinking> action) {
    action.execute(getPrivateStaticLinking());
  }

  @Nested
  public abstract CMakeLinking getPrivateSharedLinking();

  public default void privateSharedLinking(Action<? super CMakeLinking> action) {
    action.execute(getPrivateSharedLinking());
  }

}
