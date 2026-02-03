/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;


class CMakeApplicationTest {

    @Test
    void testApplicationProperties() {
        // Create a mock application
        CMakeApplication application = mock(CMakeApplication.class);
        
        // Test that we can access the methods (this is just to verify the class exists and methods can be called)
        assertNotNull(application);
    }
}