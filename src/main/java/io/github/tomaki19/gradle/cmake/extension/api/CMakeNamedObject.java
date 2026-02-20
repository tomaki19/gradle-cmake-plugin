/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.Named;

public abstract class CMakeNamedObject implements Named, Comparable<CMakeNamedObject> {

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
    CMakeNamedObject other = (CMakeNamedObject) obj;
    if (getName() == null) {
      if (other.getName() != null)
        return false;
    } else if (!getName().equals(other.getName()))
      return false;
    return true;
  }

  @Override
  public int compareTo(CMakeNamedObject other) {
    int comparator = 0;
    if ((comparator = getName().compareTo(other.getName())) != 0) {
      return comparator;
    }
    return comparator;
  }

}
