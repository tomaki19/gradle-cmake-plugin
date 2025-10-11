/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

public abstract class CMakeBinary extends CMakeBinaries implements Named {

  private Optional<String> outputName = Optional.empty();
  private final SourceDirectorySet headers;
  private final SourceDirectorySet sources;
  private Collection<String> toolchains = new HashSet<>();
  private Collection<String> privateCompileOptions = new HashSet<>();
  private Collection<String> privateCompileDefinitions = new HashSet<>();

  @Inject
  public CMakeBinary(ObjectFactory objectFactory) {
    this.headers = objectFactory.sourceDirectorySet("headers", "CMake headers");
    this.sources = objectFactory.sourceDirectorySet("sources", "CMake sources");
  }

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
    this.toolchains = values.stream().map((value) -> value.toString()).sorted().toList();
  }

  public SourceDirectorySet getHeaders() {
    return headers;
  }

  public void headers(Action<? super SourceDirectorySet> action) {
    action.execute(headers);
  }

  public SourceDirectorySet getSources() {
    return sources;
  }

  public void sources(Action<? super SourceDirectorySet> action) {
    action.execute(sources);
  }

  public Collection<String> getPrivateCompileOptions() {
    return privateCompileOptions;
  }

  public void setPrivateCompileOptions(final Collection<CharSequence> values) {
    this.privateCompileOptions = values.stream().map((value) -> value.toString()).sorted().toList();
  }

  public Collection<String> getPrivateCompileDefinitions() {
    return privateCompileDefinitions;
  }

  public void setPrivateCompileDefinitions(final Collection<CharSequence> values) {
    this.privateCompileDefinitions = values.stream().map((value) -> value.toString()).sorted().toList();
  }

}
