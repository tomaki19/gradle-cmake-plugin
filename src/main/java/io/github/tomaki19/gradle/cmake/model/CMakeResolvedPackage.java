/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeSystemPackage;

public final class CMakeResolvedPackage extends CMakeResolvedName<CMakeResolvedPackage> {

  private final Map<String, String> properties;
  private final Optional<File> interfacePath;
  private final Optional<File> staticLibraryPath;
  private final Optional<File> sharedLibraryPath;

  CMakeResolvedPackage(final CMakeSystemPackage object) {
    super(object.getName());
    this.properties = new TreeMap<>(object.getProperties());
    this.interfacePath = object.getInterfacePath();
    this.staticLibraryPath = object.getStaticLibraryPath();
    this.sharedLibraryPath = object.getSharedLibraryPath();
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public Optional<File> getInterfacePath() {
    return interfacePath;
  }

  public Optional<File> getStaticLibraryPath() {
    return staticLibraryPath;
  }

  public Optional<File> getSharedLibraryPath() {
    return sharedLibraryPath;
  }

}
