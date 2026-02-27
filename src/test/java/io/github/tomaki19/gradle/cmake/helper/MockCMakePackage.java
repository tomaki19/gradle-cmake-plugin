/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;

public class MockCMakePackage extends CMakePackage {

  private final String name;
  private final Property<Boolean> configMode;
  private final Property<String> targetPrefix;
  private final SetProperty<String> components;
  private final MapProperty<String, String> properties;

  public MockCMakePackage(final String name, final ObjectFactory factory) {
    this.name = name;
    this.configMode = factory.property(Boolean.class);
    this.targetPrefix = factory.property(String.class);
    this.components = factory.setProperty(String.class);
    this.properties = factory.mapProperty(String.class, String.class);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Property<Boolean> getModuleMode() {
    return configMode;
  }

  @Override
  public Property<String> getTargetPrefix() {
    return targetPrefix;
  }

  @Override
  public SetProperty<String> getComponents() {
    return components;
  }

  @Override
  public MapProperty<String, String> getProperties() {
    return properties;
  }

}
