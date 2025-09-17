/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Map;
import java.util.TreeMap;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeSystemPackage;

public final class CMakeResolvedSystemPackage extends CMakeResolvedName<CMakeResolvedSystemPackage> {

  private final Map<String, String> properties;

  CMakeResolvedSystemPackage(final CMakeSystemPackage object) {
    super(object.getName());
    this.properties = new TreeMap<>(object.getProperties().get());
  }

  public Map<String, String> getProperties() {
    return properties;
  }

}
