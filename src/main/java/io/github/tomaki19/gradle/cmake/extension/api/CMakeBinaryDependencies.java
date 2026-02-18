/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkType;

abstract class CMakeBinaryDependencies {

  private final Collection<String> names;
  private Optional<String> from = Optional.empty();
  private Optional<CMakeLinkType> linkage = Optional.empty();

  protected CMakeBinaryDependencies(final CharSequence... names) {
    this.names = Arrays.asList(names).stream().map((name) -> name.toString()).toList();
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

  protected void setLinkage(final CMakeLinkType value) {
    this.linkage = Optional.of(value);
  }

  public Optional<CMakeLinkType> getLinkage() {
    return linkage;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
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
    return true;
  }

}
