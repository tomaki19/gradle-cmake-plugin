/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Nested;

public interface CMakeLibraries extends CMakeBinaries {

  public final static CMakeBuildType Static = CMakeBuildType.STATIC;
  public final static CMakeBuildType Shared = CMakeBuildType.SHARED;
  public final static CMakeBuildType Module = CMakeBuildType.MODULE;

  @Nested
  public CMakeLibraryLinking getPrivateLinking();

  public default void privateLinking(Action<CMakeLibraryLinking> action) {
    action.execute(getPrivateLinking());
  }

  public SetProperty<CMakeBuildType> getBuildTypes();

  public default void buildTypes(final CMakeBuildType... values) {
    getBuildTypes().set(Arrays.asList(values));
  }

}
