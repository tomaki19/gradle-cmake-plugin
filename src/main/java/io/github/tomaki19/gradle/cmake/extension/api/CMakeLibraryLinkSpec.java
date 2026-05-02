/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.exceptions.CMakeApiException;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeLibraryLinkSpec extends CMakeBinaryLinkSpec {

  public static final String BUILD_VARIANT = "forBuild";

  private final CMakeBuildVariant buildVariant;

  public CMakeLibraryLinkSpec(final Set<String> components, final String project, final CMakeLinkVariant linkVariant,
      final CMakeVisibility visibility, final CMakeBuildVariant buildVariant) {
    super(components, project, linkVariant, visibility);
    this.buildVariant = buildVariant;
  }

  public CMakeBuildVariant getBuildVariant() {
    return buildVariant;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getBuildVariant().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeLibraryLinkSpec))
      return false;
    final CMakeLibraryLinkSpec other = (CMakeLibraryLinkSpec) obj;
    if (!getBuildVariant().equals(other.getBuildVariant()))
      return false;
    return true;
  }

  static class Init extends CMakeBinaryLinkSpec.Init {

    public static CMakeLibraryLinkSpec create(final Collection<CharSequence> components,
        final Map<String, Object> entries) throws CMakeApiException {
      validateContentTypes(entries);
      validateType(entries.get(BUILD_VARIANT), BUILD_VARIANT, CharSequence.class);
      return new CMakeLibraryLinkSpec(components.stream().map((it) -> it.toString())
          .collect(Collectors.toSet()),
          entries.containsKey(PROJECT) ? ((CharSequence) entries.get(PROJECT)).toString() : "",
          entries.containsKey(LINK_VARIANT)
              ? CMakeLinkVariant.valueOf(((CharSequence) entries.get(LINK_VARIANT)).toString().toUpperCase())
              : CMakeLinkVariant.SHARED,
          entries.containsKey(VISIBILITY)
              ? CMakeVisibility.valueOf(((CharSequence) entries.get(VISIBILITY)).toString().toUpperCase())
              : CMakeVisibility.PUBLIC,
          entries.containsKey(BUILD_VARIANT)
              ? CMakeBuildVariant.valueOf(((CharSequence) entries.get(BUILD_VARIANT)).toString().toUpperCase())
              : CMakeBuildVariant.SHARED);
    }
  }

}
