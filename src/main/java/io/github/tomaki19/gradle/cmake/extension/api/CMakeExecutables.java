/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

interface CMakeExecutables {

  @Nested
  public abstract CMakeExecutableLinking getPrivateLinking();

  public default void privateLinking(Action<CMakeExecutableLinking> action) {
    action.execute(getPrivateLinking());
  }

}
