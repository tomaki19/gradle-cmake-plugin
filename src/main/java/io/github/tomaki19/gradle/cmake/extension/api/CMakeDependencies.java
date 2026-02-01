/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkage;

public class CMakeDependencies {

  private final Collection<String> names;
  private Optional<String> from = Optional.empty();
  private Optional<CMakeLinkage> linkage = Optional.empty();

  public CMakeDependencies(CharSequence... names) {
    this.names = Arrays.asList(names).stream().map((name) -> name.toString()).toList();
  }

  public Collection<String> getNames() {
    return Collections.unmodifiableCollection(names);
  }

  public Optional<String> getFrom() {
    return from;
  }

  public CMakeDependencies from(final CharSequence value) {
    this.from = Optional.of(value.toString());
    return this;
  }

  public Optional<CMakeLinkage> getLinkage() {
    return linkage;
  }

  public CMakeDependencies getLinkStatic() {
    this.linkage = Optional.of(CMakeLinkage.STATIC);
    return this;
  }

  public CMakeDependencies getLinkShared() {
    this.linkage = Optional.of(CMakeLinkage.SHARED);
    return this;
  }

  public CMakeDependencies getLinkInterface() {
    this.linkage = Optional.of(CMakeLinkage.INTERFACE);
    return this;
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
    CMakeDependencies other = (CMakeDependencies) obj;
    if (names == null) {
      if (other.names != null)
        return false;
    } else if (!names.equals(other.names))
      return false;
    return true;
  }

}
