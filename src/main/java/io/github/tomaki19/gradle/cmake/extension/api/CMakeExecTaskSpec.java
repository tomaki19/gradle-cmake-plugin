/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;

public final class CMakeExecTaskSpec extends CMakeCustomTaskSpec<CMakeCustomExec> {

  public static final String PREFIX = "prefix";

  private final String prefix;

  public CMakeExecTaskSpec(final String prefix, final Set<String> toolchains, final Set<String> buildConfigs,
      final Set<String> components) {
    super(toolchains, buildConfigs, components);
    this.prefix = prefix;
  }

  public String getPrefix() {
    return prefix;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getPrefix().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeExecTaskSpec))
      return false;
    final CMakeExecTaskSpec other = (CMakeExecTaskSpec) obj;
    if (!getPrefix().equals(other.getPrefix()))
      return false;
    return super.equals(obj);
  }

  public static class Init extends CMakeCustomTaskSpec.Init {

    public static CMakeExecTaskSpec create(final Map<String, Object> spec)
        throws CMakeApiException {
      validateMandatory(spec.get(PREFIX), PREFIX);
      validateType(spec.get(PREFIX), PREFIX, CharSequence.class);
      validateContentTypes(spec);
      return new CMakeExecTaskSpec(spec.get(PREFIX).toString(),
          ((Collection<?>) spec.getOrDefault(TOOLCHAINS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(BUILD_CONFIGS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(COMPONENTS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()));
    }

    public static CMakeExecTaskSpec create(final Map<String, Object> spec, final String prefix)
        throws CMakeApiException {
      validateMandatory(prefix, PREFIX);
      validateNotBlank(prefix, PREFIX);
      validateContentTypes(spec);
      return new CMakeExecTaskSpec(prefix,
          ((Collection<?>) spec.getOrDefault(TOOLCHAINS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(BUILD_CONFIGS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(COMPONENTS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()));
    }
  }

}
