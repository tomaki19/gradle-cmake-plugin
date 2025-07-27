/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.SetProperty;

public interface CMakeSystemPackage extends CMakeNamedObject {

  SetProperty<String> getComponents();

  MapProperty<String, String> getProperties();

}
