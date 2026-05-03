/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public final class CMakeBuildSpec extends CMakeApiSpecInit {

  private final Set<String> names;
  private final CMakeVisibility visibility;

  private CMakeBuildSpec(final Set<String> names, final CMakeVisibility visibility) {
    this.names = Collections.unmodifiableSet(names);
    this.visibility = visibility;
  }

  public Set<String> getNames() {
    return names;
  }

  public CMakeVisibility getVisibility() {
    return visibility;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getNames().hashCode();
    result = prime * result + getVisibility().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeBuildSpec))
      return false;
    final CMakeBuildSpec other = (CMakeBuildSpec) obj;
    if (!getNames().equals(other.getNames()))
      return false;
    if (!getVisibility().equals(other.getVisibility()))
      return false;
    return true;
  }

  static class Init extends CMakeApiSpecInit {

    private static final String VISIBILITY = "visibility";

    public static CMakeBuildSpec create(final Map<String, Object> entries, final CharSequence... names)
        throws CMakeApiException {
      validateType(entries.get(VISIBILITY), VISIBILITY, CharSequence.class);
      return new CMakeBuildSpec(Arrays.asList(names).stream().map((it) -> it.toString())
          .collect(Collectors.toSet()),
          entries.containsKey(VISIBILITY)
              ? CMakeVisibility.valueOf(((CharSequence) entries.get(VISIBILITY)).toString().toUpperCase())
              : CMakeVisibility.PUBLIC);
    }

  }

}
