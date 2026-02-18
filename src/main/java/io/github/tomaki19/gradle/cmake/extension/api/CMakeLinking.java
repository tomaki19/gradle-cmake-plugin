/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public abstract class CMakeLinking {

  final Collection<String> options = new HashSet<>();

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

}
