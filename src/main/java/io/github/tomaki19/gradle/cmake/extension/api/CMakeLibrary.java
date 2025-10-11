/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;

import org.gradle.api.model.ObjectFactory;

public abstract class CMakeLibrary extends CMakeBinary {

  private Collection<String> publicCompileOptions = new HashSet<>();
  private Collection<String> publicCompileDefinitions = new HashSet<>();
  private Collection<String> publicLinkDependencies = new HashSet<>();

  @Inject
  public CMakeLibrary(ObjectFactory objectFactory) {
    super(objectFactory);
  }

  public Collection<String> getPublicCompileOptions() {
    return publicCompileOptions;
  }

  public void setPublicCompileOptions(final Collection<CharSequence> values) {
    this.publicCompileOptions = values.stream().map((value) -> value.toString()).sorted().toList();
  }

  public Collection<String> getPublicCompileDefinitions() {
    return publicCompileDefinitions;
  }

  public void setPublicCompileDefinitions(final Collection<CharSequence> values) {
    this.publicCompileDefinitions = values.stream().map((value) -> value.toString()).sorted().toList();
  }

  public Collection<String> getPublicLinkDependencies() {
    return publicLinkDependencies;
  }

  public void setPublicLinkDependencies(final Collection<CharSequence> values) {
    this.publicLinkDependencies = values.stream().map((value) -> value.toString()).sorted().toList();
  }

}
