/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Objects;

abstract class CMakeApiSpecInit {

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

}
