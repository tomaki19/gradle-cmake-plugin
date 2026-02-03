/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}