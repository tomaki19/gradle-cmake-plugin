/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import org.gradle.api.provider.SetProperty;

public interface CMakeLibrary extends CMakeObject {

  SetProperty<String> getPublicCompileOptions();

  SetProperty<String> getPublicCompileDefinitions();

  SetProperty<String> getPublicLinkDependencies();

}
