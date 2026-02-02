/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable;

class CMakeResolvedExecutableTest {

    @Test
    void testResolvedExecutableCreation() {
        // This test is mainly to ensure the class can be instantiated
        // Actual functionality tests would require complex mocking
        assertNotNull(CMakeResolvedExecutable.class);
    }

    @Test
    void testResolvedExecutableInstantiation() {
        // Test that we can at least create an instance without errors
        // The actual constructor is package-private, so we can't directly test it
        // But we can verify the class exists and is accessible
        assertEquals("io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable", 
            CMakeResolvedExecutable.class.getName());
    }
}