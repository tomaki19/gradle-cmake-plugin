/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomExec;

public final class CMakeExecTaskSpec extends CMakeCustomTaskSpec<CMakeCustomExec> {

  private final String prefix;

  public CMakeExecTaskSpec(final Set<String> toolchains, final Set<String> buildConfigs,
      final Set<String> components, final String prefix) {
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

    public static CMakeExecTaskSpec create(final Map<String, Object> spec, final CharSequence prefix)
        throws CMakeApiException {
      if (Objects.isNull(prefix)) {
        throw new CMakeApiException("Exec task prefix is missing!");
      }
      if (prefix.toString().isBlank()) {
        throw new CMakeApiException("Exec task prefix is empty!");
      }
      validateContentTypes(spec);
      return new CMakeExecTaskSpec(
          ((Collection<?>) spec.getOrDefault(TOOLCHAINS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(BUILD_CONFIGS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(COMPONENTS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          prefix.toString());
    }
  }

}
