/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.Action;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

public class CMakeCustomTaskProto<T extends AbstractArchiveTask> {

  private final Class<T> type;
  private final Action<AbstractArchiveTask> action;

  public CMakeCustomTaskProto(final Class<T> type, final Action<AbstractArchiveTask> action) {
    this.type = type;
    this.action = action;
  }

  public Class<T> getType() {
    return type;
  }

  public Action<AbstractArchiveTask> getAction() {
    return action;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CMakeCustomTaskProto<?> other = (CMakeCustomTaskProto<?>) obj;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

}
