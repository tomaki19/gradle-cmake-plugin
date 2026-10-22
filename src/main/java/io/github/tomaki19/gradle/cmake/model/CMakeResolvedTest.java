/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class CMakeResolvedTest extends CMakeResolvedBinary<CMakeResolvedTest> {

  private final boolean testResultsXmlOutput;

  public CMakeResolvedTest(final CMakeTest test, final boolean stripDebug, final String projectVersion) {
    this(test, stripDebug, projectVersion, false);
  }

  public CMakeResolvedTest(final CMakeTest test, final boolean stripDebug, final String projectVersion,
      final boolean testResultsXmlOutput) {
    super(test, stripDebug, projectVersion);
    this.testResultsXmlOutput = testResultsXmlOutput;
  }

  /** Whether ctest writes a JUnit XML report for this test. */
  public boolean isTestResultsXmlOutput() {
    return testResultsXmlOutput;
  }

}
