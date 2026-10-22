/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

public class CMakeLibraryLinkSpec extends CMakeBinaryLinkSpec {

  public static final String BUILD_VARIANT = "forBuild";

  private final Set<CMakeBuildVariant> buildVariants;

  public CMakeLibraryLinkSpec(final Set<String> components, final String project, final CMakeLinkVariant linkVariant,
      final CMakeVisibility visibility, final Set<CMakeBuildVariant> buildVariants) {
    super(components, project, linkVariant, visibility);
    this.buildVariants = EnumSet.noneOf(CMakeBuildVariant.class);
    this.buildVariants.addAll(buildVariants);
  }

  /**
   * Returns the build variants of the linking library this dependency applies to; empty means all variants.
   */
  public Set<CMakeBuildVariant> getBuildVariants() {
    return Collections.unmodifiableSet(buildVariants);
  }

  /**
   * Returns whether this dependency applies when the linking library is built as the given variant.
   * {@link CMakeLinkVariant#INTERFACE} libraries only receive dependencies without a {@code forBuild} restriction.
   */
  public boolean appliesTo(final CMakeLinkVariant libraryVariant) {
    if (buildVariants.isEmpty()) {
      return true;
    }
    return switch (libraryVariant) {
      case SHARED -> buildVariants.contains(CMakeBuildVariant.SHARED);
      case STATIC -> buildVariants.contains(CMakeBuildVariant.STATIC);
      case INTERFACE -> false;
    };
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getBuildVariants().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeLibraryLinkSpec))
      return false;
    if (!super.equals(obj))
      return false;
    final CMakeLibraryLinkSpec other = (CMakeLibraryLinkSpec) obj;
    if (!getBuildVariants().equals(other.getBuildVariants()))
      return false;
    return true;
  }

  static class Init extends CMakeBinaryLinkSpec.Init {

    public static CMakeLibraryLinkSpec create(final Map<String, Object> entries, final CharSequence... components)
        throws CMakeApiException {
      validateContentTypes(entries);
      return new CMakeLibraryLinkSpec(Arrays.asList(components).stream().map((it) -> it.toString())
          .collect(Collectors.toSet()),
          entries.containsKey(PROJECT) ? ((CharSequence) entries.get(PROJECT)).toString() : "",
          entries.containsKey(LINK_VARIANT)
              ? (CMakeLinkVariant) entries.get(LINK_VARIANT)
              : CMakeLinkVariant.SHARED,
          toVisibility(entries.get(VISIBILITY), VISIBILITY),
          toBuildVariants(entries.get(BUILD_VARIANT)));
    }

    /**
     * Accepts one build variant or a collection of them, each given as {@link CMakeBuildVariant}, as the
     * {@code SHARED}/{@code STATIC} constants of a {@code linking} block ({@link CMakeLinkVariant}) or as a name in
     * any case (e.g. {@code 'static'}).
     */
    private static Set<CMakeBuildVariant> toBuildVariants(final Object entry) throws CMakeApiException {
      if (Objects.isNull(entry)) {
        return Collections.emptySet();
      }
      final Collection<?> values = entry instanceof Collection<?> collection ? collection : List.of(entry);
      final Set<CMakeBuildVariant> result = EnumSet.noneOf(CMakeBuildVariant.class);
      for (final Object value : values) {
        result.add(toBuildVariant(value));
      }
      return result;
    }

    private static CMakeBuildVariant toBuildVariant(final Object value) throws CMakeApiException {
      final String name;
      if (value instanceof CMakeBuildVariant variant) {
        name = variant.name();
      } else if (value instanceof CMakeLinkVariant variant) {
        name = variant.name();
      } else if (value instanceof CharSequence text) {
        name = text.toString().toUpperCase(Locale.ROOT);
      } else {
        throw new CMakeApiException("Invalid %s of type %s!".formatted(BUILD_VARIANT,
            Objects.isNull(value) ? null : value.getClass()));
      }
      if (CMakeBuildVariant.SHARED.name().equals(name)) {
        return CMakeBuildVariant.SHARED;
      }
      if (CMakeBuildVariant.STATIC.name().equals(name)) {
        return CMakeBuildVariant.STATIC;
      }
      throw new CMakeApiException("Invalid %s '%s': use SHARED or STATIC!".formatted(BUILD_VARIANT, value));
    }
  }

}
