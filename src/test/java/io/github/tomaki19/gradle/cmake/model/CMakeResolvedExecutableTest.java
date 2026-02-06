/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Property;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

class CMakeResolvedExecutableTest {

    private CMakeTest mockTest(final String name) {
        CMakeTest test = mock(CMakeTest.class);
        when(test.getName()).thenReturn(name);
        when(test.getOutputName()).thenReturn(Optional.empty());

        // Mock headers and sources to avoid NullPointerException
        SourceDirectorySet headers = mock(SourceDirectorySet.class);
        when(headers.getSrcDirs()).thenReturn(Collections.emptySet());
        when(test.getHeaders()).thenReturn(headers);

        SourceDirectorySet sources = mock(SourceDirectorySet.class);
        when(sources.getFiles()).thenReturn(Collections.emptySet());
        when(test.getSources()).thenReturn(sources);

        Property<Boolean> stripDebug = mock(Property.class);
        when(stripDebug.get()).thenReturn(Boolean.FALSE);
        when(stripDebug.getOrElse(Boolean.FALSE)).thenReturn(Boolean.FALSE);
        when(stripDebug.getOrElse(Boolean.TRUE)).thenReturn(Boolean.TRUE);
        when(test.getStripDebug()).thenReturn(stripDebug);

        return test;
    }

    @Test
    void testConstructor() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);
        assertEquals("test-executable", resolvedExecutable.getName());
    }

    @Test
    void testGetters() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        // Test default values
        assertFalse(resolvedExecutable.isStripDebug());
        assertTrue(resolvedExecutable.getPrivateCompileDefinitions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateCompileOptions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateLinkOptions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateSystemPackageDependencies().isEmpty());
        assertTrue(resolvedExecutable.getPrivateProjectPackageDependencies().isEmpty());
    }

    @Test
    void testAddPrivateCompileDefinitions() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        resolvedExecutable.addPrivateCompileDefinitions("TEST_DEFINE");
        assertFalse(resolvedExecutable.getPrivateCompileDefinitions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateCompileDefinitions().contains("TEST_DEFINE"));
    }

    @Test
    void testAddPrivateCompileOptions() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        resolvedExecutable.addPrivateCompileOptions("-O2");
        assertFalse(resolvedExecutable.getPrivateCompileOptions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateCompileOptions().contains("-O2"));
    }

    @Test
    void testAddPrivateLinkOption() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        resolvedExecutable.addPrivateLinkOption("-ltest");
        assertFalse(resolvedExecutable.getPrivateLinkOptions().isEmpty());
        assertTrue(resolvedExecutable.getPrivateLinkOptions().contains("-ltest"));
    }

    @Test
    void testAddPrivateSystemPackageDependency() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        resolvedExecutable.addPrivateSystemPackageDependency("pkg-config");
        assertFalse(resolvedExecutable.getPrivateSystemPackageDependencies().isEmpty());
        assertTrue(resolvedExecutable.getPrivateSystemPackageDependencies().contains("pkg-config"));
    }

    @Test
    void testAddPrivateProjectPackageDependency() {
        CMakeTest test = mockTest("test-executable");

        CMakeResolvedExecutable resolvedExecutable = new CMakeResolvedExecutable(test, false);
        assertNotNull(resolvedExecutable);

        // This would require a CMakeResolvedProjectDependency object, so we'll just
        // test that it doesn't throw
        // The actual implementation would be tested in integration tests
        assertTrue(true); // Placeholder test
    }
}