/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public abstract class CMakeSystemPackage implements CMakeNamedObject {

  private Map<String, String> properties = new TreeMap<>();
  private Optional<File> interfacePath = Optional.empty();
  private Optional<File> staticLibraryPath = Optional.empty();
  private Optional<File> sharedLibraryPath = Optional.empty();

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(final Map<CharSequence, CharSequence> values) {
    this.properties = values.entrySet().parallelStream().collect(
        Collectors.toUnmodifiableMap((entry) -> entry.getKey().toString(), (entry) -> entry.getValue().toString()));
  }

  public Optional<File> getInterfacePath() {
    return interfacePath;
  }

  public void setInterfacePath(File value) {
    this.interfacePath = Optional.of(value);
  }

  public Optional<File> getStaticLibraryPath() {
    return staticLibraryPath;
  }

  public void getStaticLibraryPath(File value) {
    this.staticLibraryPath = Optional.of(value);
  }

  public Optional<File> getSharedLibraryPath() {
    return sharedLibraryPath;
  }

  public void getSharedLibraryPath(File value) {
    this.sharedLibraryPath = Optional.of(value);
  }
}
