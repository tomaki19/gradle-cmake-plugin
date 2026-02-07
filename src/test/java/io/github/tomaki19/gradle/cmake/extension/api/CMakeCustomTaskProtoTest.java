/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
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
    
    @Test
    void testGetters() {
        // Create a mock toolchain
        CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("test-toolchain");
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create a custom task proto
        CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
        
        // Verify all getters work correctly
        assertEquals("test-task", proto.getName());
        assertEquals("test-toolchain", proto.getToolchainName());
        assertEquals("debug", proto.getBuildConfig());
        assertNotNull(proto.getEnvironmentFile());
    }
    
    @Test
    void testGettersWithFile() {
        // Create a mock toolchain
        CMakeToolchain toolchain = mock(CMakeToolchain.class);
        File testFile = new File("test.txt");
        when(toolchain.getName()).thenReturn("test-toolchain");
        when(toolchain.getEnvironmentFile()).thenReturn(Optional.of(testFile));
        
        // Create a custom task proto
        CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", toolchain, "debug");
        
        // Verify all getters work correctly
        assertEquals("test-task", proto.getName());
        assertEquals("test-toolchain", proto.getToolchainName());
        assertEquals("debug", proto.getBuildConfig());
        assertNotNull(proto.getEnvironmentFile());
        assertTrue(proto.getEnvironmentFile().isPresent());
        assertEquals(testFile, proto.getEnvironmentFile().get());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Create mock toolchains
        CMakeToolchain toolchain1 = mock(CMakeToolchain.class);
        when(toolchain1.getName()).thenReturn("test-toolchain");
        when(toolchain1.getEnvironmentFile()).thenReturn(Optional.empty());
        
        CMakeToolchain toolchain2 = mock(CMakeToolchain.class);
        when(toolchain2.getName()).thenReturn("test-toolchain");
        when(toolchain2.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create two protos with same values
        CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
        CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");
        
        // Verify equals and hashCode work correctly
        assertEquals(proto1, proto2);
        assertEquals(proto1.hashCode(), proto2.hashCode());
    }
    
    @Test
    void testEqualsWithDifferentName() {
        // Create mock toolchains
        CMakeToolchain toolchain1 = mock(CMakeToolchain.class);
        when(toolchain1.getName()).thenReturn("test-toolchain");
        when(toolchain1.getEnvironmentFile()).thenReturn(Optional.empty());
        
        CMakeToolchain toolchain2 = mock(CMakeToolchain.class);
        when(toolchain2.getName()).thenReturn("test-toolchain");
        when(toolchain2.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create two protos with different names
        CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task1", toolchain1, "debug");
        CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task2", toolchain2, "debug");
        
        // Verify they are not equal
        assertNotEquals(proto1, proto2);
    }
    
    @Test
    void testEqualsWithDifferentToolchainName() {
        // Create mock toolchains
        CMakeToolchain toolchain1 = mock(CMakeToolchain.class);
        when(toolchain1.getName()).thenReturn("toolchain1");
        when(toolchain1.getEnvironmentFile()).thenReturn(Optional.empty());
        
        CMakeToolchain toolchain2 = mock(CMakeToolchain.class);
        when(toolchain2.getName()).thenReturn("toolchain2");
        when(toolchain2.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create two protos with different toolchain names
        CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
        CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "debug");
        
        // Verify they are not equal
        assertNotEquals(proto1, proto2);
    }
    
    @Test
    void testEqualsWithDifferentBuildConfig() {
        // Create mock toolchains
        CMakeToolchain toolchain1 = mock(CMakeToolchain.class);
        when(toolchain1.getName()).thenReturn("test-toolchain");
        when(toolchain1.getEnvironmentFile()).thenReturn(Optional.empty());
        
        CMakeToolchain toolchain2 = mock(CMakeToolchain.class);
        when(toolchain2.getName()).thenReturn("test-toolchain");
        when(toolchain2.getEnvironmentFile()).thenReturn(Optional.empty());
        
        // Create two protos with different build configs
        CMakeCustomTaskProto proto1 = new CMakeCustomTaskProto("test-task", toolchain1, "debug");
        CMakeCustomTaskProto proto2 = new CMakeCustomTaskProto("test-task", toolchain2, "release");
        
        // Verify they are not equal
        assertNotEquals(proto1, proto2);
    }
    
    @Test
    void testEqualsWithNull() {
        // Create a proto
        CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", mock(CMakeToolchain.class), "debug");
        
        // Verify equals with null returns false
        assertFalse(proto.equals(null));
    }
    
    @Test
    void testEqualsWithDifferentClass() {
        // Create a proto
        CMakeCustomTaskProto proto = new CMakeCustomTaskProto("test-task", mock(CMakeToolchain.class), "debug");
        
        // Verify equals with different class returns false
        assertFalse(proto.equals("not a proto"));
    }
}