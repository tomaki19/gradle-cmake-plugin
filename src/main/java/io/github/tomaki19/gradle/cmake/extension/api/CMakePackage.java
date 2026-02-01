/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gradle.api.Named;

public abstract class CMakePackage implements Named {

  private Optional<Boolean> configMode = Optional.empty();
  private Optional<String> targetPrefix = Optional.empty();
  private Collection<String> components = new HashSet<>();
  private Map<String, String> properties = new TreeMap<>();
  private Collection<Path> interfaces = new HashSet<>();
  private Collection<Path> staticLibraries = new HashSet<>();
  private Collection<Path> sharedLibraries = new HashSet<>();

  public Optional<Boolean> getConfigMode() {
    return configMode;
  }

  public void setConfigMode(final Boolean value) {
    this.configMode = Optional.of(value);
  }

  public Optional<String> getTargetPrefix() {
    return targetPrefix;
  }

  public void setTargetPrefix(final String value) {
    this.targetPrefix = Optional.of(value);
  }

  public Collection<String> getComponents() {
    return Collections.unmodifiableCollection(components);
  }

  public void setComponents(final Collection<CharSequence> values) {
    this.components = values.stream().map((value) -> value.toString()).sorted().toList();
  }

  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(properties);
  }

  public void setProperties(final Map<CharSequence, CharSequence> values) {
    this.properties = values.entrySet().stream().collect(
        Collectors.toUnmodifiableMap((entry) -> entry.getKey().toString(), (entry) -> entry.getValue().toString()));
  }

  public Collection<Path> getInterfaces() {
    return Collections.unmodifiableCollection(interfaces);
  }

  public void setInterfaces(Collection<CharSequence> values) {
    this.interfaces = values.stream().map((value) -> Paths.get(value.toString())).sorted().toList();
  }

  public Collection<Path> getStaticLibraries() {
    return Collections.unmodifiableCollection(staticLibraries);
  }

  public void setStaticLibraries(Collection<CharSequence> values) {
    this.staticLibraries = values.stream().map((value) -> Paths.get(value.toString())).sorted().toList();
  }

  public Collection<Path> getSharedLibraries() {
    return Collections.unmodifiableCollection(sharedLibraries);
  }

  public void setSharedLibraries(Collection<CharSequence> values) {
    this.sharedLibraries = values.stream().map((value) -> Paths.get(value.toString())).sorted().toList();
  }

}
