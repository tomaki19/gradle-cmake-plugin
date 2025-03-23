
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.SetProperty;

public interface CMakeFindPackage extends CMakeNamedObject {

  public SetProperty<String> getComponents();

  public MapProperty<String, String> getProperties();
}
