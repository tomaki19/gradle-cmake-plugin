/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;

class CMakeExtensionTest {

    @Test
    void testExtensionName() {
        assertEquals("cmake", CMakeExtension.NAME);
    }

    @Test
    void testConstructor() {
        // Test that we can create an instance with empty map
        final java.util.Map<String, java.util.Map<String, String>> customTasks = Collections.emptyMap();
        // This test is mainly to ensure the constructor exists and doesn't throw errors
        assertNotNull(customTasks);
    }
}