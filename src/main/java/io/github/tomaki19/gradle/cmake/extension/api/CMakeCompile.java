/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;

public class CMakeCompile {

  private final Collection<String> defines = new HashSet<>();
  private final Collection<String> options = new HashSet<>();

  public Collection<String> getDefines() {
    return defines;
  }

  public void define(String value) {
    defines.add(value);
  }

  public void defines(Collection<String> values) {
    defines.addAll(values);
  }

  public Collection<String> getOptions() {
    return options;
  }

  public void option(String value) {
    options.add(value);
  }

  public void options(Collection<String> values) {
    options.addAll(values);
  }

}
