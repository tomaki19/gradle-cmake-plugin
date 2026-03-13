/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

public abstract class CMakeBinaryDependencies {

  private final Set<String> names = new HashSet<>();
  private Optional<String> from = Optional.empty();
  private Optional<CMakeLinkType> linkType = Optional.empty();
  private boolean visibilityPrivate;

  protected CMakeBinaryDependencies(final boolean defaultPrivate, final CharSequence... names) {
    this.visibilityPrivate = defaultPrivate;
    this.names.addAll(Arrays.asList(names).stream().map((name) -> name.toString()).toList());
  }

  public Collection<String> getNames() {
    return Collections.unmodifiableCollection(names);
  }

  protected void setFrom(final String value) {
    this.from = Optional.of(value);
  }

  public Optional<String> getFrom() {
    return from;
  }

  protected void setLinkType(final CMakeLinkType value) {
    this.linkType = Optional.of(value);
  }

  public Optional<CMakeLinkType> getLinkType() {
    return linkType;
  }

  protected void setVisibilityPrivate(final boolean value) {
    this.visibilityPrivate = value;
  }

  public boolean isPrivate() {
    return visibilityPrivate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
    result = prime * result + ((from == null) ? 0 : from.hashCode());
    result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
    result = prime * result + (visibilityPrivate ? 1231 : 1237);
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
    if (linkType == null) {
      if (other.linkType != null)
        return false;
    } else if (!linkType.equals(other.linkType))
      return false;
    if (visibilityPrivate != other.visibilityPrivate)
      return false;
    return true;
  }

}
