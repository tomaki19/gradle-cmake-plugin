/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import org.gradle.api.provider.SetProperty;

public interface CMakeLibrary extends CMakeBinary, CMakeLibraries {

  SetProperty<String> getPublicCompileOptions();

  SetProperty<String> getPublicCompileDefinitions();

  SetProperty<String> getPublicLinkDependencies();

}
