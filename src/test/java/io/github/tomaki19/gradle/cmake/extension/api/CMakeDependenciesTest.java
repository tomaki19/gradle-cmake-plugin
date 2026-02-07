/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class CMakeDependenciesTest {

    @Test
    void testConstructorVarargs() {
        final CMakeDependencies deps = new CMakeDependencies("lib1", "lib2");
        assertNotNull(deps);
        Collection<String> names = deps.getNames();
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("lib1"));
        assertTrue(names.contains("lib2"));
    }

    @Test
    void testGetNames() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        Collection<String> names = deps.getNames();
        assertNotNull(names);
        assertTrue(names.contains("lib1"));
    }

    @Test
    void testGetFrom() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        assertTrue(deps.getFrom().isEmpty());
    }

    @Test
    void testFrom() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        deps.from("myproject");
        assertTrue(deps.getFrom().isPresent());
        assertEquals("myproject", deps.getFrom().get());
    }

    @Test
    void testGetLinkage() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        assertTrue(deps.getLinkage().isEmpty());
    }

    @Test
    void testGetLinkStatic() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        final CMakeDependencies result = deps.getLinkStatic();
        assertNotNull(result);
        assertTrue(deps.getLinkage().isPresent());
    }

    @Test
    void testGetLinkShared() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        final CMakeDependencies result = deps.getLinkShared();
        assertNotNull(result);
        assertTrue(deps.getLinkage().isPresent());
    }

    @Test
    void testGetLinkInterface() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        final CMakeDependencies result = deps.getLinkInterface();
        assertNotNull(result);
        assertTrue(deps.getLinkage().isPresent());
    }

    @Test
    void testHashCode() {
        final CMakeDependencies deps1 = new CMakeDependencies("lib1");
        final CMakeDependencies deps2 = new CMakeDependencies("lib1");
        assertEquals(deps1.hashCode(), deps2.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        assertEquals(deps, deps);
    }

    @Test
    void testEqualsNull() {
        final CMakeDependencies deps = new CMakeDependencies("lib1");
        assertEquals(false, deps.equals(null));
    }

    @Test
    void testEqualsDifferentNames() {
        final CMakeDependencies deps1 = new CMakeDependencies("lib1");
        final CMakeDependencies deps2 = new CMakeDependencies("lib2");
        assertEquals(false, deps1.equals(deps2));
    }

    @Test
    void testEqualsSameNames() {
        final CMakeDependencies deps1 = new CMakeDependencies("lib1", "lib2");
        final CMakeDependencies deps2 = new CMakeDependencies("lib1", "lib2");
        assertEquals(true, deps1.equals(deps2));
    }
}
