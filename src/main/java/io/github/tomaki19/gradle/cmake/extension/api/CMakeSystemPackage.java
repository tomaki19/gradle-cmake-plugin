/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.provider.MapProperty;

public interface CMakeSystemPackage extends CMakeNamedObject {

  MapProperty<String, String> getProperties();

}
