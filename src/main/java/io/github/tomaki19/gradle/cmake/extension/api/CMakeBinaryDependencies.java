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

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public abstract class CMakeBinaryDependencies {

  private final Set<String> names = new HashSet<>();
  private String from = "";
  private CMakeLinkVariant linkVariant;
  private CMakeVisibility visibility;

  protected CMakeBinaryDependencies(final CMakeLinkVariant defaultLinkVariant,
      final CMakeVisibility defaultVisibility, final CharSequence... names) {
    this.linkVariant = defaultLinkVariant;
    this.visibility = defaultVisibility;
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

  protected void setLinkVariant(final CMakeLinkVariant variant) {
    this.linkVariant = variant;
  }

  public CMakeLinkVariant getLinkVariant() {
    return linkVariant;
  }

  protected void setVisibility(final CMakeVisibility visibility) {
    this.visibility = visibility;
  }

  public CMakeVisibility getVisibility() {
    return visibility;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((linkVariant == null) ? 0 : linkVariant.hashCode());
    result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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
    if (linkVariant != other.linkVariant)
      return false;
    if (visibility != other.visibility)
      return false;
    return true;
  }

}
