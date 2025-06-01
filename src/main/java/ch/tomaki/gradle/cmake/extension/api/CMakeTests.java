/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import org.gradle.api.provider.Property;

public interface CMakeTests extends CMakeBinaries {

  Property<Boolean> getTestResultsXmlOutput();

}
