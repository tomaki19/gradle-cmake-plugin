/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashSet;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;

abstract class CMakeAbstractInterface implements CMakeResolvedNamedObject {

  private final String name;
  private final Set<String> headers;

  CMakeAbstractInterface(final CMakeBinary object) throws IllegalArgumentException {
    this.name = object.getName();
    this.headers = new HashSet<>(object.getHeaders().get());
  }

  @Override
  public String getName() {
    return name;
  }

  public Set<String> getHeaders() {
    return headers;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    CMakeAbstractInterface other = (CMakeAbstractInterface) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
