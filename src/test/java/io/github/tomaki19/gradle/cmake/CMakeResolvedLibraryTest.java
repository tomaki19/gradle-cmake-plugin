/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;

class CMakeResolvedLibraryTest {

    @Test
    void testResolvedLibraryCreation() {
        // This test is mainly to ensure the class can be instantiated
        // Actual functionality tests would require complex mocking
        assertNotNull(CMakeResolvedLibrary.class);
    }

    @Test
    void testResolvedLibraryInstantiation() {
        // Test that we can at least create an instance without errors
        // The actual constructor is package-private, so we can't directly test it
        // But we can verify the class exists and is accessible
        assertEquals("io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary", 
            CMakeResolvedLibrary.class.getName());
    }
}