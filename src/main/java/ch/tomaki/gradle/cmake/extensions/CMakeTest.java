
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.Property;

public interface CMakeTest extends CMakeBinary {

  public Property<Boolean> getTestResultsXmlOutput();
}
