/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;

class CMakeResolvedPackageTest {

    @Test
    void testConstructor() {
        CMakePackage pkg = mock(CMakePackage.class);
        when(pkg.getName()).thenReturn("test-package");
        
        CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
        assertNotNull(resolvedPackage);
        assertEquals("test-package", resolvedPackage.getName());
    }

    @Test
    void testGetters() {
        CMakePackage pkg = mock(CMakePackage.class);
        when(pkg.getName()).thenReturn("test-package");
        
        CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(pkg);
        assertNotNull(resolvedPackage);
        
        // Test that the name is correctly retrieved
        assertEquals("test-package", resolvedPackage.getName());
    }
}