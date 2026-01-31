/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeResolvedToolchainTest {

    @Test
    void testToolchainCreation() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        assertNotNull(resolvedToolchain);
        // No getToolchain method exists, so we can't test this
        // assertEquals(toolchain, resolvedToolchain.getToolchain());
    }

    @Test
    void testGetBuildConfigs() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<String> buildConfigs = resolvedToolchain.getBuildConfigs();
        assertNotNull(buildConfigs);
        assertEquals(2, buildConfigs.size());
    }

    @Test
    void testGetPackages() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedPackage> packages = resolvedToolchain.getPackages();
        assertNotNull(packages);
        // Should be empty initially
        assertEquals(0, packages.size());
    }

    @Test
    void testGetInterfaceLibraries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary> libraries = resolvedToolchain.getInterfaceLibraries();
        assertNotNull(libraries);
        // Should be empty initially
        assertEquals(0, libraries.size());
    }

    @Test
    void testGetStaticLibraries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary> libraries = resolvedToolchain.getStaticLibraries();
        assertNotNull(libraries);
        // Should be empty initially
        assertEquals(0, libraries.size());
    }

    @Test
    void testGetSharedLibraries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary> libraries = resolvedToolchain.getSharedLibraries();
        assertNotNull(libraries);
        // Should be empty initially
        assertEquals(0, libraries.size());
    }

    @Test
    void testGetApplications() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable> applications = resolvedToolchain.getApplications();
        assertNotNull(applications);
        // Should be empty initially
        assertEquals(0, applications.size());
    }

    @Test
    void testGetTests() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        
        final Collection<io.github.tomaki19.gradle.cmake.model.CMakeResolvedExecutable> tests = resolvedToolchain.getTests();
        assertNotNull(tests);
        // Should be empty initially
        assertEquals(0, tests.size());
    }

}