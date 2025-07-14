/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.Project;

public class CMakeResolvedProjectPackage {

    private final CMakeResolvedToolchain toolchain;
    private final Project project;

    public CMakeResolvedProjectPackage(CMakeResolvedToolchain toolchain, Project project) {
        this.toolchain = toolchain;
        this.project = project;
    }

    public CMakeResolvedToolchain getToolchain() {
        return toolchain;
    }

    public Project getProject() {
        return project;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((toolchain == null) ? 0 : toolchain.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
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
        CMakeResolvedProjectPackage other = (CMakeResolvedProjectPackage) obj;
        if (toolchain == null) {
            if (other.toolchain != null)
                return false;
        } else if (!toolchain.equals(other.toolchain))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return project.getName() + ":" + toolchain.getName() + " [toolchain=" + toolchain.hashCode()
                + ", project=" + project.hashCode() + "] > " + hashCode();
    }
}
