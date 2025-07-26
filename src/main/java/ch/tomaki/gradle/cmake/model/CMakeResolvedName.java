/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

public abstract class CMakeResolvedName<T extends CMakeResolvedName<T>> implements Comparable<T> {

    private final String name;

    CMakeResolvedName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CMakeResolvedName<?> other = (CMakeResolvedName<?>) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int compareTo(T other) {
        int comparator = 0;
        if ((comparator = getName().compareTo(other.getName())) != 0) {
            return comparator;
        }
        return comparator;
    }

}
