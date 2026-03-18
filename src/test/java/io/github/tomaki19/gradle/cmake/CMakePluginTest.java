/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakePluginTest {

    @Test
    void load() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);
        assertNotNull(project.getPlugins().findPlugin("io.github.tomaki19.gradle-cmake-plugin"));
    }

    @Test
    void extension() {
        final Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply(CMakePlugin.class);
        assertNotNull(project.getExtensions().getByName("cmake"));
    }

    @Test
    void testPluginConstructor() {
        // Test that we can create an instance (this tests the constructor)
        // The constructor is injected by Gradle, so we just verify it can be instantiated
        assertNotNull(new CMakePlugin(null));
    }
}
