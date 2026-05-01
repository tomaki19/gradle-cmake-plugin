/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import java.util.Map;

import org.gradle.api.tasks.bundling.AbstractArchiveTask;

public final class CMakeArchiveTaskSpec<T extends AbstractArchiveTask> extends CMakeCustomTaskSpec<T> {

  private static final String TYPE = "type";

  private final Class<T> defaultType;

  public CMakeArchiveTaskSpec(Map<String, Object> entries, Class<T> defaultType) {
    super(entries);
    this.defaultType = defaultType;
  }

  public void validate() throws IllegalArgumentException {
    super.validateType(TYPE, AbstractArchiveTask.class);
    super.validateContentTypes();
  }

  @SuppressWarnings("unchecked")
  public Class<T> getType() {
    return (Class<T>) spec.getOrDefault(TYPE, defaultType);
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
    final CMakeArchiveTaskSpec<?> other = (CMakeArchiveTaskSpec<?>) obj;
    if (!getType().equals(other.getType()))
      return false;
    return super.equals(obj);
  }

}
