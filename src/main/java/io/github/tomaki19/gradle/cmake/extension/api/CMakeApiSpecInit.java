/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Locale;
import java.util.Objects;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

abstract class CMakeApiSpecInit {

  /**
   * Accepts a {@link CMakeVisibility} constant or its name in any case (e.g. {@code 'Public'}); defaults to
   * {@link CMakeVisibility#PUBLIC} when absent.
   */
  protected static CMakeVisibility toVisibility(final Object entry, final String name) throws CMakeApiException {
    if (Objects.isNull(entry)) {
      return CMakeVisibility.PUBLIC;
    }
    if (entry instanceof CMakeVisibility visibility) {
      return visibility;
    }
    if (entry instanceof CharSequence text) {
      try {
        return CMakeVisibility.valueOf(text.toString().toUpperCase(Locale.ROOT));
      } catch (final IllegalArgumentException e) {
        throw new CMakeApiException("Invalid %s '%s'!".formatted(name, text));
      }
    }
    throw new CMakeApiException("Invalid %s of type %s!".formatted(name, entry.getClass()));
  }

  protected static void validateType(final Object entry, final String name, final Class<?> type)
      throws CMakeApiException {
    if (Objects.nonNull(entry) && !(type.isAssignableFrom(entry.getClass()))) {
      throw new CMakeApiException("Invalid %s of type %s!".formatted(name, entry.getClass()));
    }
  }

  protected static void validateMandatory(final Object entry, final String name) throws CMakeApiException {
    if (Objects.isNull(entry)) {
      throw new CMakeApiException("Missing mandatory %s!".formatted(name));
    }
  }

  protected static void validateNotBlank(final String entry, final String name) throws CMakeApiException {
    if (entry != null && entry.isBlank()) {
      throw new CMakeApiException("Invalid %s: must not be blank!".formatted(name));
    }
  }

}
