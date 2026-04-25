/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.Action;

public final class CMakeCustomExecProto {

  private final String name;
  private final Action<CMakeCustomExec> action;

  public CMakeCustomExecProto(final String name, final Action<CMakeCustomExec> action) {
    this.name = name;
    this.action = action;
  }

  public String getName() {
    return name;
  }

  public Action<CMakeCustomExec> getAction() {
    return action;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof CMakeCustomExecProto))
      return false;
    CMakeCustomExecProto other = (CMakeCustomExecProto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
