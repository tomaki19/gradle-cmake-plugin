/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.GradleException;

public final class CMakeApiException extends GradleException {

  public CMakeApiException(final String message) {
    super(message);
  }

  public CMakeApiException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

}
