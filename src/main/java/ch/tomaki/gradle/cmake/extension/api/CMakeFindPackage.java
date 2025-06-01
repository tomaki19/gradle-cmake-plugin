/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.SetProperty;

public interface CMakeFindPackage extends CMakeNamedObject {

  SetProperty<String> getComponents();

  MapProperty<String, String> getProperties();

}
