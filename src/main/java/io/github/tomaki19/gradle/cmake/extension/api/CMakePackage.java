/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;


import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public abstract class CMakePackage extends CMakeNamedObject {

  public abstract Property<Boolean> getConfigMode();

  public abstract Property<String> getTargetPrefix();

  public abstract SetProperty<String> getComponents();

  public abstract MapProperty<String, String> getProperties();

}
