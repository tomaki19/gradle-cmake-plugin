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

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibilityType;

public abstract class CMakeBinaryDependencies {

  private final Set<String> names = new HashSet<>();
  private String from = "";
  private CMakeLinkType linkType;
  private CMakeVisibilityType visibilityType;

  protected CMakeBinaryDependencies(final CMakeLinkType defaultLinkType,
      final CMakeVisibilityType defaultVisibilityType, final CharSequence... names) {
    this.linkType = defaultLinkType;
    this.visibilityType = defaultVisibilityType;
    this.names.addAll(Arrays.asList(names).stream().map((name) -> name.toString()).toList());
  }

  public Collection<String> getNames() {
    return Collections.unmodifiableCollection(names);
  }

  protected void setFrom(final String value) {
    this.from = value;
  }

  public String getFrom() {
    return from;
  }

  protected void setLinkType(final CMakeLinkType type) {
    this.linkType = type;
  }

  public CMakeLinkType getLinkType() {
    return linkType;
  }

  protected void setVisibilityType(final CMakeVisibilityType type) {
    this.visibilityType = type;
  }

  public CMakeVisibilityType getVisibilityType() {
    return visibilityType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
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
    CMakeBinaryDependencies other = (CMakeBinaryDependencies) obj;
    if (names == null) {
      if (other.names != null)
        return false;
    } else if (!names.equals(other.names))
      return false;
    if (from == null) {
      if (other.from != null)
        return false;
    } else if (!from.equals(other.from))
      return false;
    if (linkType != other.linkType)
      return false;
    if (visibilityType != other.visibilityType)
      return false;
    return true;
  }

}
