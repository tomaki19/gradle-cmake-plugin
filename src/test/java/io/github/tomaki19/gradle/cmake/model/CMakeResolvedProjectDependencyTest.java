/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;


class CMakeResolvedProjectDependencyTest {

    @Test
    void testConstructor() {
        Project project = ProjectBuilder.builder().withName("test-project").build();
        
        CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib", java.util.Optional.empty());
        assertNotNull(dependency);
        assertEquals("test-lib", dependency.getName());
    }

    @Test
    void testGetters() {
        Project project = ProjectBuilder.builder().withName("test-project").build();
        
        CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib", java.util.Optional.of(CMakeLinkage.STATIC));
        assertNotNull(dependency);
        
        // Test that the name is correctly retrieved
        assertEquals("test-lib", dependency.getName());
        // Test that the linkage is correctly retrieved
        assertEquals("static", dependency.getLinkage());
    }
}