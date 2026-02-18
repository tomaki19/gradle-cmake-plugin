/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public interface CMakeLibraries extends CMakeBinaries {

  @Nested
  public abstract CMakeLibraryLinking getPrivateLinking();

  public default void privateLinking(Action<CMakeLibraryLinking> action) {
    action.execute(getPrivateLinking());
  }

  public abstract Property<Boolean> getBuildStatic();

  public abstract Property<Boolean> getBuildShared();

}
