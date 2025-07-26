/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeSystemPackage;

public final class CMakeResolvedSystemPackage extends CMakeResolvedName<CMakeResolvedSystemPackage> {

  private final Collection<String> components;
  private final Map<String, String> properties;

  CMakeResolvedSystemPackage(final CMakeSystemPackage object) {
    super(object.getName());
    this.components = new TreeSet<>(object.getComponents().get());
    this.properties = new TreeMap<>(object.getProperties().get());
  }

  public Collection<String> getComponents() {
    return components;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

}
