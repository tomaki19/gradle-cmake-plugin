/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import org.gradle.api.GradleException;

public final class CMakeResolverException extends GradleException {

  public CMakeResolverException(final String message) {
    super(message);
  }

  public CMakeResolverException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

}
