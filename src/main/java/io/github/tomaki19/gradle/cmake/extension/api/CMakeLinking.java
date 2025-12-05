/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;

public class CMakeLinking {

  final Collection<String> options = new HashSet<>();
  final Collection<CMakeDependencies> dependencyCollection = new HashSet<>();

  public Collection<String> getOptions() {
    return options;
  }

  public void option(String value) {
    options.add(value);
  }

  public void options(String... values) {
    for (String value : values) {
      options.add(value);
    }
  }

  public Collection<CMakeDependencies> getDependencies() {
    return dependencyCollection;
  }

  public CMakeDependencies dependency(CharSequence name) {
    final CMakeDependencies dependencies = new CMakeDependencies(name);
    dependencyCollection.add(dependencies);
    return dependencies;
  }

  public CMakeDependencies dependencies(CharSequence... names) {
    final CMakeDependencies dependencies = new CMakeDependencies(names);
    dependencyCollection.add(dependencies);
    return dependencies;
  }

}
