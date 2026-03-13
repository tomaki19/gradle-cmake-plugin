/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Nested;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildType;

public interface CMakeLibraries extends CMakeBinaries {

  public final static CMakeBuildType Static = CMakeBuildType.STATIC;
  public final static CMakeBuildType Shared = CMakeBuildType.SHARED;
  public final static CMakeBuildType Module = CMakeBuildType.MODULE;

  public SetProperty<CMakeBuildType> getBuildTypes();

  public default void buildTypes(final CMakeBuildType... values) {
    getBuildTypes().set(Arrays.asList(values));
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
