/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import org.gradle.api.Action;
import org.gradle.api.Task;

public final class CMakeCustomTaskProto<T extends Task> {

    private final String name;
    private final Class<T> type;
    private final Action<T> action;

    public CMakeCustomTaskProto(final String name, final Class<T> type, final Action<T> action) {
        this.name = name;
        this.type = type;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public Action<T> getAction() {
        return action;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        CMakeCustomTaskProto other = (CMakeCustomTaskProto) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }


}
