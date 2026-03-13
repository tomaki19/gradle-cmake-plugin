/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

interface CMakeExecutables {

  @Nested
  public CMakeExecutableCompiling getCompiling();

  public default void compiling(Action<CMakeExecutableCompiling> action) {
    action.execute(getCompiling());
  }

  @Nested
  public abstract CMakeExecutableLinking getLinking();

  public default void linking(Action<CMakeExecutableLinking> action) {
    action.execute(getLinking());
  }

}
