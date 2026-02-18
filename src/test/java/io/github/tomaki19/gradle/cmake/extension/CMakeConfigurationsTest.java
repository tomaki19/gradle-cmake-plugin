/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.junit.jupiter.api.Test;


class CMakeConfigurationsTest {

    @Test
    void testConfigurationsValues() {
        // Test that we can access the enum values
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
        // Test that configure() returns a non-null action
        Action<Configuration> action = CMakeConfigurations.CMAKE_COMPILE.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeCompile() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_COMPILE.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeCompileClasspath() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_COMPILE_CLASSPATH.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeCompileElements() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_COMPILE_ELEMENTS.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeRuntime() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_RUNTIME.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeRuntimeClasspath() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_RUNTIME_CLASSPATH.configure();
        assertNotNull(action);
    }

    @Test
    void testConfigureActionCmakeRuntimeElements() {
        Action<Configuration> action = CMakeConfigurations.CMAKE_RUNTIME_ELEMENTS.configure();
        assertNotNull(action);
    }
}
