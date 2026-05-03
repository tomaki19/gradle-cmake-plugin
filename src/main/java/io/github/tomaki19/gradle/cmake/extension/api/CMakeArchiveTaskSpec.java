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

import org.gradle.api.tasks.bundling.AbstractArchiveTask;

public final class CMakeArchiveTaskSpec extends CMakeCustomTaskSpec<AbstractArchiveTask> {

  private static final String TYPE = "type";

  private final Class<AbstractArchiveTask> type;

  public CMakeArchiveTaskSpec(final Set<String> toolchains, final Set<String> buildConfigs,
      final Set<String> components, Class<AbstractArchiveTask> type) {
    super(toolchains, buildConfigs, components);
    this.type = type;
  }

  public Class<AbstractArchiveTask> getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getType().hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeArchiveTaskSpec))
      return false;
    final CMakeArchiveTaskSpec other = (CMakeArchiveTaskSpec) obj;
    if (!getType().equals(other.getType()))
      return false;
    return super.equals(obj);
  }

  public static class Init extends CMakeCustomTaskSpec.Init {

    @SuppressWarnings("unchecked")
    public static CMakeArchiveTaskSpec create(final Map<String, Object> spec,
        final Class<?> defaultType)
        throws CMakeApiException {
      validateContentTypes(spec);
      return new CMakeArchiveTaskSpec(
          ((Collection<?>) spec.getOrDefault(TOOLCHAINS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(BUILD_CONFIGS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          ((Collection<?>) spec.getOrDefault(COMPONENTS, Collections.emptyList())).stream()
              .map((it) -> it.toString()).collect(Collectors.toSet()),
          (Class<AbstractArchiveTask>) spec.getOrDefault(TYPE, defaultType));
    }

  }

}
