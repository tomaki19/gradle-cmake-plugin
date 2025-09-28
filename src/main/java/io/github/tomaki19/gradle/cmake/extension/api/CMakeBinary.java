/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CMakeBinary extends CMakeBinaries implements CMakeNamedObject {

  private Optional<String> outputName = Optional.empty();
  private Collection<String> toolchains = new HashSet<>();
  private Collection<String> headers = new HashSet<>();
  private Collection<String> sources = new HashSet<>();
  private Collection<String> privateCompileOptions = new HashSet<>();
  private Collection<String> privateCompileDefinitions = new HashSet<>();

  public Optional<String> getOutputName() {
    return outputName;
  }

  public void setOutputName(final CharSequence value) {
    this.outputName = Optional.of(value.toString());
  }

  public Collection<String> getToolchains() {
    return toolchains;
  }

  public void setToolchains(final Collection<CharSequence> values) {
    this.toolchains = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

  public Collection<String> getHeaders() {
    return headers;
  }

  public void setHeaders(final Collection<CharSequence> values) {
    this.headers = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

  public Collection<String> getSources() {
    return sources;
  }

  public void setSources(final Collection<CharSequence> values) {
    this.sources = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

  public Collection<String> getPrivateCompileOptions() {
    return privateCompileOptions;
  }

  public void setPrivateCompileOptions(final Collection<CharSequence> values) {
    this.privateCompileOptions = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

  public Collection<String> getPrivateCompileDefinitions() {
    return privateCompileDefinitions;
  }

  public void setPrivateCompileDefinitions(final Collection<CharSequence> values) {
    this.privateCompileDefinitions = values.parallelStream().map((value) -> value.toString())
        .collect(Collectors.toUnmodifiableSet());
  }

}
