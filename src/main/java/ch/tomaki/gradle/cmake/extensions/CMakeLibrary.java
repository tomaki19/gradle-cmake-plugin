
/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extensions;

import org.gradle.api.provider.SetProperty;

public interface CMakeLibrary extends CMakeBinary {

  public SetProperty<String> getPublicCompileOptions();

  public SetProperty<String> getPublicCompileDefinitions();

  public SetProperty<String> getPublicLinkDependencies();
}
