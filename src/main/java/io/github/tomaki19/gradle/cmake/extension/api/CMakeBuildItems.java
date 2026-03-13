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

public class CMakeBuildItems {

  private final Set<String> names = new HashSet<>();
  private boolean visibilityPrivate;

  public CMakeBuildItems(final boolean defaultPrivate, final CharSequence... names) {
    this.visibilityPrivate = defaultPrivate;
    this.names.addAll(Arrays.asList(names).stream().map((name) -> name.toString()).toList());
  }

  public Collection<String> getNames() {
    return Collections.unmodifiableCollection(names);
  }

  public boolean isPrivate() {
    return visibilityPrivate;
  }

  public CMakeBuildItems setPrivate() {
    this.visibilityPrivate = true;
    return this;
  }

  public CMakeBuildItems setPublic() {
    this.visibilityPrivate = false;
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((names == null) ? 0 : names.hashCode());
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
    CMakeBuildItems other = (CMakeBuildItems) obj;
    if (names == null) {
      if (other.names != null)
        return false;
    } else if (!names.equals(other.names))
      return false;
    if (visibilityPrivate != other.visibilityPrivate)
      return false;
    return true;
  }

}
