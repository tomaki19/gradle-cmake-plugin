/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;


class CMakeConfigurationsTest {

    @Test
    void testConfigurationsValues() {
        assertNotNull(CMakeConfigurations.CMAKE_COMPILE);
        assertNotNull(CMakeConfigurations.CMAKE_COMPILE_ELEMENTS);
        assertNotNull(CMakeConfigurations.CMAKE_RUNTIME);
        assertNotNull(CMakeConfigurations.CMAKE_RUNTIME_ELEMENTS);
    }

    @Test
    void testToString() {
        assertEquals("cmakeCompile", CMakeConfigurations.CMAKE_COMPILE.toString());
        assertEquals("cmakeCompileClasspath", CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.toString());
        assertEquals("cmakeCompileElements", CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.toString());
        assertEquals("cmakeRuntime", CMakeConfigurations.CMAKE_RUNTIME.toString());
        assertEquals("cmakeRuntimeClasspath", CMakeConfigurations.CMAKE_RUNTIME_CLASSPATH.toString());
        assertEquals("cmakeRuntimeElements", CMakeConfigurations.CMAKE_RUNTIME_ELEMENTS.toString());
    }

    @Test
    void testConfigureAction() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_COMPILE.configure();
        assertNotNull(action);
    }

    @Test
    void testExecuteConfigureActions() {
        final Project project = ProjectBuilder.builder().build();
        for (CMakeConfigurations config : CMakeConfigurations.values()) {
            Configuration configuration = project.getConfigurations().create(config.toString());
            config.configure().execute(configuration);
            assertNotNull(configuration.getDescription());
        }
    }
}
