/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension.api;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public interface CMakeBinaries {

  SetProperty<String> getPrivateLinkDependencies();

  Property<Boolean> getBuildStatic();

  Property<Boolean> getBuildShared();

  Property<Boolean> getStripDebug();

  Property<Boolean> getPackageBuildOutputs();

}
