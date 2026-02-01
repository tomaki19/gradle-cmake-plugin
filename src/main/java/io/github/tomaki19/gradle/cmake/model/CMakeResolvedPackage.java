/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.nio.file.Path;
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
  private final Collection<Path> interfaces;
  private final Collection<Path> staticLibraries;
  private final Collection<Path> sharedLibraries;

  CMakeResolvedPackage(final CMakePackage object) {
    super(object.getName());
    this.properties = new TreeMap<>(object.getProperties());
    this.components = new HashSet<>(object.getComponents());
    this.configMode = object.getConfigMode().orElse(Boolean.FALSE);
    this.interfaces = object.getInterfaces();
    this.staticLibraries = object.getStaticLibraries();
    this.sharedLibraries = object.getSharedLibraries();
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

  public Collection<Path> getInterfaces() {
    return Collections.unmodifiableCollection(interfaces);
  }

  public Collection<Path> getStaticLibraries() {
    return Collections.unmodifiableCollection(staticLibraries);
  }

  public Collection<Path> getSharedLibraries() {
    return Collections.unmodifiableCollection(sharedLibraries);
  }

}
