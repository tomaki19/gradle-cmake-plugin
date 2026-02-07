/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakePackageTest {

    @Test
    void testGetConfigMode() {
        final CMakePackage pkg = new TestCMakePackage();
        assertTrue(pkg.getConfigMode().isEmpty());
    }

    @Test
    void testSetConfigMode() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setConfigMode(true);
        assertTrue(pkg.getConfigMode().isPresent());
        assertEquals(true, pkg.getConfigMode().get());
    }

    @Test
    void testGetTargetPrefix() {
        final CMakePackage pkg = new TestCMakePackage();
        assertTrue(pkg.getTargetPrefix().isEmpty());
    }

    @Test
    void testSetTargetPrefix() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setTargetPrefix("/usr/local");
        assertTrue(pkg.getTargetPrefix().isPresent());
        assertEquals("/usr/local", pkg.getTargetPrefix().get());
    }

    @Test
    void testGetComponents() {
        final CMakePackage pkg = new TestCMakePackage();
        Collection<String> components = pkg.getComponents();
        assertNotNull(components);
        assertTrue(components.isEmpty());
    }

    @Test
    void testSetComponents() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setComponents(Arrays.asList("component1", "component2"));
        assertEquals(2, pkg.getComponents().size());
        assertTrue(pkg.getComponents().contains("component1"));
        assertTrue(pkg.getComponents().contains("component2"));
    }

    @Test
    void testGetProperties() {
        final CMakePackage pkg = new TestCMakePackage();
        Map<String, String> properties = pkg.getProperties();
        assertNotNull(properties);
        assertTrue(properties.isEmpty());
    }

    @Test
    void testSetProperties() {
        final CMakePackage pkg = new TestCMakePackage();
        Map<CharSequence, CharSequence> values = new HashMap<>();
        values.put("key1", "value1");
        values.put("key2", "value2");
        pkg.setProperties(values);
        assertEquals(2, pkg.getProperties().size());
        assertEquals("value1", pkg.getProperties().get("key1"));
        assertEquals("value2", pkg.getProperties().get("key2"));
    }

    @Test
    void testGetInterfaces() {
        final CMakePackage pkg = new TestCMakePackage();
        Collection<Path> interfaces = pkg.getInterfaces();
        assertNotNull(interfaces);
        assertTrue(interfaces.isEmpty());
    }

    @Test
    void testSetInterfaces() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setInterfaces(Arrays.asList("/path/to/interface1", "/path/to/interface2"));
        assertEquals(2, pkg.getInterfaces().size());
    }

    @Test
    void testGetStaticLibraries() {
        final CMakePackage pkg = new TestCMakePackage();
        Collection<Path> libraries = pkg.getStaticLibraries();
        assertNotNull(libraries);
        assertTrue(libraries.isEmpty());
    }

    @Test
    void testSetStaticLibraries() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setStaticLibraries(Arrays.asList("/path/to/lib1", "/path/to/lib2"));
        assertEquals(2, pkg.getStaticLibraries().size());
    }

    @Test
    void testGetSharedLibraries() {
        final CMakePackage pkg = new TestCMakePackage();
        Collection<Path> libraries = pkg.getSharedLibraries();
        assertNotNull(libraries);
        assertTrue(libraries.isEmpty());
    }

    @Test
    void testSetSharedLibraries() {
        final CMakePackage pkg = new TestCMakePackage();
        pkg.setSharedLibraries(Arrays.asList("/path/to/lib1", "/path/to/lib2"));
        assertEquals(2, pkg.getSharedLibraries().size());
    }

    private static class TestCMakePackage extends CMakePackage {
        @Override
        public String getName() {
            return "test";
        }
    }
}
