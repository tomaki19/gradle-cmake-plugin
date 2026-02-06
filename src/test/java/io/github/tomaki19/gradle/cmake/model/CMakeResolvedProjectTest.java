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

class CMakeResolvedProjectTest {

    @Test
    void testConstructor() {
        Project project = ProjectBuilder.builder().withName("test-project").build();
        
        CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
        assertNotNull(resolvedProject);
        assertEquals("test-project", resolvedProject.getName());
    }

    @Test
    void testGetters() {
        Project project = ProjectBuilder.builder().withName("test-project").build();
        
        CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
        assertNotNull(resolvedProject);
        
        // Test that the name is correctly retrieved
        assertEquals("test-project", resolvedProject.getName());
    }
}