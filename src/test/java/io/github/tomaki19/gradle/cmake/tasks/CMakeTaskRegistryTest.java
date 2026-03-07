/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;


class CMakeTaskRegistryTest {

    @Test
    void testTaskRegistryGroups() {
        assertEquals("cmake build", CMakeTaskRegistry.GROUP_BUILD);
        assertEquals("cmake test", CMakeTaskRegistry.GROUP_CHECK);
        assertEquals("cmake install", CMakeTaskRegistry.GROUP_INSTALL);
    }

    @Test
    void testTaskRegistryMethods() {
        // Test that we can create a registry instance
        CMakeTaskRegistry registry = new CMakeTaskRegistry();
        assertNotNull(registry);
    }
}
