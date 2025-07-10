/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Set;

import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

abstract class CMakeResolvedInterface extends CMakeResolvedObject {

    private final CMakeResolvedToolchain toolchain;
    private final Set<String> headers;

    CMakeResolvedInterface(final CMakeBinary binary, final CMakeToolchain toolchain) {
        super(binary.getName());
        this.toolchain = new CMakeResolvedToolchain(toolchain);
        this.headers = binary.getHeaders().get();
    }

    public CMakeResolvedToolchain getToolchain() {
        return toolchain;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CMakeResolvedInterface other = (CMakeResolvedInterface) obj;
        if (toolchain == null) {
            if (other.toolchain != null)
                return false;
        } else if (!toolchain.equals(other.toolchain))
            return false;
        return true;
    }

}
