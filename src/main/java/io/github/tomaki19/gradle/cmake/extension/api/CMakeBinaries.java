/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

interface CMakeBinaries {

  @Nested
  public abstract CMakeCompile getPrivateCompile();

  public default void privateCompile(Action<CMakeCompile> action) {
    action.execute(getPrivateCompile());
  }

  public Property<Boolean> getStripDebug();

}
