/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;

import org.gradle.api.tasks.Nested;

public abstract class CMakeLinking {

  @Nested
  public abstract Collection<CMakeDependencies> getDependencies();

  public CMakeDependencies dependency(CharSequence name) {
    final CMakeDependencies dependencies = new CMakeDependencies(name);
    getDependencies().add(dependencies);
    return dependencies;
  }

  public CMakeDependencies dependencies(CharSequence... names) {
    final CMakeDependencies dependencies = new CMakeDependencies(names);
    getDependencies().add(dependencies);
    return dependencies;
  }

  @Nested
  public abstract Collection<String> getOptions();

  public void option(String value) {
    getOptions().add(value);
  }

  public void options(String... values) {
    for (String value : values) {
      option(value);
    }
  }

}
