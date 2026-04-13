/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public abstract class CMakeBinary extends CMakeNamedObject implements CMakeBinaries {

  private final Collection<String> toolchains = new HashSet<>();
  private final SourceDirectorySet headers;
  private final SourceDirectorySet sources;

  @Inject
  public CMakeBinary(ObjectFactory objectFactory) {
    this.headers = objectFactory.sourceDirectorySet("headers", "");
    this.sources = objectFactory.sourceDirectorySet("sources", "");
  }

  public abstract Property<String> getOutputName();

  public Collection<String> getToolchains() {
    return Collections.unmodifiableCollection(toolchains);
  }

  public void toolchains(final CharSequence... values) {
    toolchains.addAll(Arrays.asList(values).stream().map((value) -> value.toString()).toList());
  }

  public void toolchains(final Collection<String> values) {
    toolchains.addAll(values);
  }

  public SourceDirectorySet getHeaders() {
    return headers.isEmpty() ? headers.srcDir(Paths.get("src", getName(), "headers")) : headers;
  }

  public void headers(Action<? super SourceDirectorySet> action) {
    action.execute(headers);
  }

  public SourceDirectorySet getSources() {
    return sources.isEmpty() ? sources.srcDir(Paths.get("src", getName(), "sources")) : sources;
  }

  public void sources(Action<? super SourceDirectorySet> action) {
    action.execute(sources);
  }

}
