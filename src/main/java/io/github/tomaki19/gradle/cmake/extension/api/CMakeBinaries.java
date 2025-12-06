/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface CMakeBinaries {

  @Nested
  public abstract CMakeCompile getPrivateCompile();

  public default void privateCompile(Action<? super CMakeCompile> action) {
    action.execute(getPrivateCompile());
  }

  @Nested
  public abstract CMakeLinking getPrivateLinking();

  public default void privateLinking(Action<? super CMakeLinking> action) {
    action.execute(getPrivateLinking());
  }

  public Property<Boolean> getStripDebug();

  public Property<Boolean> getPackageBuildOutputs();

}
