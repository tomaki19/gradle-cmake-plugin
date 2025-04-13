/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import org.gradle.api.provider.Property;

public interface CMakeTest extends CMakeBinary {

  Property<Boolean> getTestResultsXmlOutput();
}
