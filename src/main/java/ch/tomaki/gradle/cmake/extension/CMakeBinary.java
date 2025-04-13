/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface CMakeBinary extends CMakeNamedObject {

  SetProperty<String> getBuildToolchains();

  SetProperty<String> getIncludes();

  SetProperty<String> getSources();

  SetProperty<String> getPrivateCompileOptions();

  SetProperty<String> getPrivateCompileDefinitions();

  SetProperty<String> getPrivateLinkDependencies();

  Property<Boolean> getBuildStatic();

  Property<Boolean> getBuildShared();

  Property<Boolean> getStripDebug();

  Property<Boolean> getPackageBuildOutputs();
}
