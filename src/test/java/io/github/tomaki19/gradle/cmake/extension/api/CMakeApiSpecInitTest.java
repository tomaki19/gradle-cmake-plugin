/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.exceptions.CMakeApiException;

class CMakeApiSpecInitTest {

  @Test
  void validateType_wrongType_throws() {
    assertThrows(CMakeApiException.class, () -> CMakeExecutableLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.PROJECT, 123), "mylib"));
  }

  @Test
  void validateMandatory_null_throws() {
    assertThrows(CMakeApiException.class, () -> {
      final CMakeApiSpecInitHelper helper = new CMakeApiSpecInitHelper();
      helper.callValidateMandatory(null, "testField");
    });
  }

  @Test
  void validateMandatory_nonNull_noThrow() throws CMakeApiException {
    final CMakeApiSpecInitHelper helper = new CMakeApiSpecInitHelper();
    helper.callValidateMandatory("value", "testField");
  }

  private static class CMakeApiSpecInitHelper extends CMakeApiSpecInit {
    void callValidateMandatory(final Object entry, final String name) throws CMakeApiException {
      validateMandatory(entry, name);
    }
  }
}
