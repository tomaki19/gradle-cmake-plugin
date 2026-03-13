/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public class CMakeBuildItems {

  private final Set<String> names = new HashSet<>();
  private CMakeVisibilityType visibilityType;

  public CMakeBuildItems(final CMakeVisibilityType defaultVisibilityType, final CharSequence... names) {
    this.visibilityType = defaultVisibilityType;
    this.names.addAll(Arrays.asList(names).stream().map((name) -> name.toString()).toList());
  }

  public Collection<String> getNames() {
    return Collections.unmodifiableCollection(names);
  }

  protected CMakeVisibilityType visibility(final CMakeVisibilityType type) {
    this.visibilityType = type;
    return type;
  }

  public CMakeVisibilityType getVisibilityType() {
    return visibilityType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
    result = prime * result + ((visibilityType == null) ? 0 : visibilityType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeBuildItems other = (CMakeBuildItems) obj;
    if (names == null) {
      if (other.names != null)
        return false;
    } else if (!names.equals(other.names))
      return false;
    if (visibilityType != other.visibilityType)
      return false;
    return true;
  }

}
