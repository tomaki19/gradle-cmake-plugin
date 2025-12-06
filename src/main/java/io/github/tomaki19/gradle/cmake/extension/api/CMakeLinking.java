/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;

public class CMakeLinking {

  final Collection<String> options = new HashSet<>();
  final Collection<CMakeDependencies> dependencies = new HashSet<>();

  public Collection<String> getOptions() {
    return options;
  }

  public void option(CharSequence value) {
    options.add(value.toString());
  }

  public void options(CharSequence... values) {
    for (CharSequence value : values) {
      options.add(value.toString());
    }
  }

  public Collection<CMakeDependencies> getDependencies() {
    return dependencies;
  }

  public CMakeDependencies dependency(CharSequence name) {
    final CMakeDependencies entry = new CMakeDependencies(name);
    dependencies.add(entry);
    return entry;
  }

  public CMakeDependencies dependencies(CharSequence... names) {
    final CMakeDependencies entry = new CMakeDependencies(names);
    dependencies.add(entry);
    return entry;
  }

}
