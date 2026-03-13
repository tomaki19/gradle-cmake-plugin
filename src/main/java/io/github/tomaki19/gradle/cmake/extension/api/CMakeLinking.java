/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

abstract class CMakeLinking {

  final Collection<CMakeBuildItems> options = new HashSet<>();
  private boolean visibilityPrivate;

  CMakeLinking(final boolean defaultPrivate) {
    this.visibilityPrivate = defaultPrivate;
  }

  public Collection<CMakeBuildItems> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public CMakeBuildItems options(final CharSequence... values) {
    final CMakeBuildItems entry = new CMakeBuildItems(visibilityPrivate, values);
    options.add(entry);
    return entry;
  }

  public void options(final Collection<CMakeBuildItems> entries) {
    options.addAll(entries);
  }

}
