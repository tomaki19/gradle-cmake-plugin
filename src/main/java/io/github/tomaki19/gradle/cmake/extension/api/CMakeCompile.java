/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class CMakeCompile {

  private final Collection<String> defines = new HashSet<>();
  private final Collection<String> options = new HashSet<>();

  public Collection<String> getDefines() {
    return Collections.unmodifiableCollection(defines);
  }

  public void define(CharSequence value) {
    defines.add(value.toString());
  }

  public void defines(CharSequence... values) {
    for (final CharSequence value : values) {
      defines.add(value.toString());
    }
  }

  public Collection<String> getOptions() {
    return Collections.unmodifiableCollection(options);
  }

  public void option(CharSequence value) {
    options.add(value.toString());
  }

  public void options(CharSequence... values) {
    for (final CharSequence value : values) {
      options.add(value.toString());
    }
  }

}
