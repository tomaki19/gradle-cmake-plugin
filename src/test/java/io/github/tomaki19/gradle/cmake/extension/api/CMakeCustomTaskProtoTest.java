/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;


class CMakeCustomTaskProtoTest {

    @Test
    void testConstructor() {
        // Create a mock toolchain
        CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("test-toolchain");
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create a custom task proto
        CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
        
        // Verify properties
        assertEquals("test-task", proto.getName());
        assertEquals("test-toolchain", proto.getToolchainName());
        assertEquals("debug", proto.getBuildConfig());
        assertNotNull(proto.getEnvironmentFile());
    }
}