/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.provider.Property;

public interface CMakeTests extends CMakeBinaries {

  Property<Boolean> getTestResultsXmlOutput();

}
