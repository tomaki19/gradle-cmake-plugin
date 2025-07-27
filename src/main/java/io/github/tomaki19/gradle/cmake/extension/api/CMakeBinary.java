/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface CMakeBinary extends CMakeNamedObject, CMakeInterface, CMakeBinaries {

  Property<String> getOutputName();

  SetProperty<String> getToolchains();

  SetProperty<String> getSources();

  SetProperty<String> getPrivateCompileOptions();

  SetProperty<String> getPrivateCompileDefinitions();

}
