/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;

import org.gradle.api.tasks.Nested;

public abstract class CMakeCompile {

  @Nested
  public abstract Collection<String> getDefines();

  public void define(String value) {
    getDefines().add(value);
  }

  public void defines(Collection<String> values) {
    getDefines().addAll(values);
  }

  @Nested
  public abstract Collection<String> getOptions();

  public void option(String value) {
    getOptions().add(value);
  }

  public void options(Collection<String> values) {
    getOptions().addAll(values);
  }

}
