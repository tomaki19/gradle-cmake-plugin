/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CMakeResolverExceptionTest {

  @Test
  void constructor_messageAndCause() {
    final Throwable cause = new RuntimeException("root cause");
    final CMakeResolverException ex = new CMakeResolverException("test message", cause);
    assertEquals("test message", ex.getMessage());
    assertEquals(cause, ex.getCause());
  }
}
