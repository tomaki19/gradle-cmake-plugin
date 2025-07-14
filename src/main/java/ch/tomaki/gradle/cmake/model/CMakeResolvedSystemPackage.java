/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeSystemPackage;

public class CMakeResolvedSystemPackage extends CMakeResolvedName {

  private final Set<String> components;
  private final Map<String, String> properties;

  CMakeResolvedSystemPackage(final CMakeSystemPackage object) {
    super(object.getName());
    this.components = object.getComponents().get();
    this.properties = object.getProperties().get();
  }

  public Set<String> getComponents() {
    return components;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

}
