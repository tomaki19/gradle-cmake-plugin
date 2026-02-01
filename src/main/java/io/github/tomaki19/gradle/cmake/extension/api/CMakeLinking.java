/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CMakeLinking {

  final Collection<String> options = new HashSet<>();
  final Collection<CMakeDependencies> dependencies = new HashSet<>();

  public Collection<String> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public void option(final CharSequence value) {
    options.add(value.toString());
  }

  public void options(final CharSequence... values) {
    for (CharSequence value : values) {
      options.add(value.toString());
    }
  }

  public void options(final Collection<String> values) {
    options.addAll(values);
  }

  public Collection<CMakeDependencies> getDependencies() {
    return Collections.unmodifiableCollection(dependencies);
  }

  public CMakeDependencies dependency(final CharSequence name) {
    final CMakeDependencies entry = new CMakeDependencies(name);
    dependencies.add(entry);
    return entry;
  }

  public void dependency(final CMakeDependencies entry) {
    dependencies.add(entry);
  }

  public CMakeDependencies dependencies(final CharSequence... names) {
    final CMakeDependencies entry = new CMakeDependencies(names);
    dependencies.add(entry);
    return entry;
  }

  public void dependencies(final Collection<CMakeDependencies> entries) {
    dependencies.addAll(entries);
  }

}
