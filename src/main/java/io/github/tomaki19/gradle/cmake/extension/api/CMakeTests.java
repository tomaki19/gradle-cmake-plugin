/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Optional;

public abstract class CMakeTests implements CMakeBinaries {

  private Optional<Boolean> testResultsXmlOutput = Optional.empty();

  public Optional<Boolean> getTestResultsXmlOutput() {
    return testResultsXmlOutput;
  }

  public void setTestResultsXmlOutput(final Boolean value) {
    this.testResultsXmlOutput = Optional.of(value);
  }
}
