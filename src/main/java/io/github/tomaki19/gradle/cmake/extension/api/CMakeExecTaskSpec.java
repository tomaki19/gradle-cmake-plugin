/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Map;

import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;

public final class CMakeExecTaskSpec extends CMakeCustomTaskSpec<CMakeCustomExec> {

  public static final String NAME = "name";

  public CMakeExecTaskSpec(Map<String, Object> entries) {
    super(entries);
  }

  public void validate() throws IllegalArgumentException {
    super.validateType(NAME, CharSequence.class);
    super.validateMandatory(NAME);
    validateNotEmptyName();
    super.validateContentTypes();
  }

  public String getName() {
    return (String) spec.getOrDefault(NAME, "");
  }

  private void validateNotEmptyName() throws IllegalArgumentException {
    if (getName().isBlank()) {
      throw new IllegalArgumentException("Missing mandatory %s!".formatted(NAME));
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getName().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeExecTaskSpec))
      return false;
    final CMakeExecTaskSpec other = (CMakeExecTaskSpec) obj;
    if (!getName().equals(other.getName()))
      return false;
    return super.equals(obj);
  }

}
