/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.exceptions.CMakeApiException;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeExecutableLinkSpec extends CMakeBinaryLinkSpec {

  public CMakeExecutableLinkSpec(final Set<String> components, final String project,
      final CMakeLinkVariant linkVariant, final CMakeVisibility visibility) {
    super(components, project, linkVariant, visibility);
  }

  static class Init extends CMakeBinaryLinkSpec.Init {

    public static CMakeExecutableLinkSpec create(final Map<String, Object> entries, final CharSequence... components)
        throws CMakeApiException {
      validateContentTypes(entries);
      return new CMakeExecutableLinkSpec(Arrays.asList(components).stream().map((it) -> it.toString())
          .collect(Collectors.toSet()),
          entries.containsKey(PROJECT) ? ((CharSequence) entries.get(PROJECT)).toString() : "",
          entries.containsKey(LINK_VARIANT)
              ? CMakeLinkVariant.valueOf(((CharSequence) entries.get(LINK_VARIANT)).toString().toUpperCase())
              : CMakeLinkVariant.SHARED,
          entries.containsKey(VISIBILITY)
              ? CMakeVisibility.valueOf(((CharSequence) entries.get(VISIBILITY)).toString().toUpperCase())
              : CMakeVisibility.PUBLIC);
    }

  }

}
