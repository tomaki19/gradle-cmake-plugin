/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.gradle.internal.os.OperatingSystem;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

class CMakeResolvedToolchainTest {

    @Test
    void testResolvedToolchainCreation() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertNotNull(resolvedToolchain);
        assertEquals("TestToolchain", resolvedToolchain.getName());
    }

    @Test
    void testResolvedToolchainWithEmptyBuildConfigs() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.emptyList());
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertNotNull(resolvedToolchain);
        assertTrue(resolvedToolchain.getBuildConfigs().isEmpty());
    }

    @Test
    void testHasBinaries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertFalse(resolvedToolchain.hasBinaries());
    }

    @Test
    void testHasApplications() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertFalse(resolvedToolchain.hasApplications());
    }

    @Test
    void testHasTests() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertFalse(resolvedToolchain.hasTests());
    }

    @Test
    void testHasInterfaceLibraries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertFalse(resolvedToolchain.hasInterfaceLibraries());
    }

    @Test
    void testHasBinaryLibraries() {
        final CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("TestToolchain");
        when(toolchain.getOperatingSystem()).thenReturn(OperatingSystem.current());
        when(toolchain.getBuildConfigs()).thenReturn(Collections.singletonList("Debug"));
        when(toolchain.getGenerator()).thenReturn(Optional.empty());
        when(toolchain.getEnvironment()).thenReturn(Collections.emptyMap());
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        when(toolchain.getToolchainFile()).thenReturn(Optional.empty());

        final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
        assertFalse(resolvedToolchain.hasBinaryLibraries());
    }
}