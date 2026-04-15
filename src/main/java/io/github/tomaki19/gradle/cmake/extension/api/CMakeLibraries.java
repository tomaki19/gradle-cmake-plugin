/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Nested;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;

public interface CMakeLibraries extends CMakeBinaries {

  public static final CMakeBuildVariant STATIC = CMakeBuildVariant.STATIC;
  public static final CMakeBuildVariant SHARED = CMakeBuildVariant.SHARED;

  public SetProperty<CMakeBuildVariant> getBuildVariants();

  public default void buildVariants(final CMakeBuildVariant... values) {
    getBuildVariants().set(Arrays.asList(values));
  }

  @Nested
  public CMakeLibraryCompiling getCompiling();

  public default void compiling(Action<CMakeLibraryCompiling> action) {
    action.execute(getCompiling());
  }

  @Nested
  public CMakeLibraryLinking getLinking();

  public default void linking(Action<CMakeLibraryLinking> action) {
    action.execute(getLinking());
  }

}
