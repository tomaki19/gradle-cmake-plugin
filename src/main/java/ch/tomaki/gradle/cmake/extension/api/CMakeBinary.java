/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import org.gradle.api.provider.SetProperty;

public interface CMakeBinary extends CMakeNamedObject, CMakeInterface, CMakeBinaries {

  SetProperty<String> getToolchains();

  SetProperty<String> getSources();

  SetProperty<String> getPrivateCompileOptions();

  SetProperty<String> getPrivateCompileDefinitions();

}
