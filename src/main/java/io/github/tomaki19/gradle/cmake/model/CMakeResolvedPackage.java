/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;

public final class CMakeResolvedPackage extends CMakeResolvedName<CMakeResolvedPackage> {

  private final Map<String, String> properties;
  private final Collection<String> components;
  private final boolean configMode;

  CMakeResolvedPackage(final CMakePackage object) {
    super(object.getName());
    this.configMode = object.getConfigMode().getOrElse(Boolean.FALSE);
    this.properties = new TreeMap<>(object.getProperties().get());
    this.components = new HashSet<>(object.getComponents().get());
  }

  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(properties);
  }

  public Collection<String> getComponents() {
    return Collections.unmodifiableCollection(components);
  }

  public boolean isConfigMode() {
    return configMode;
  }

}
