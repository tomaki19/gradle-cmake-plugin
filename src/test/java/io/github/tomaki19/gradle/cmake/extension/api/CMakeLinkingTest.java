/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class CMakeLinkingTest {

    @Test
    void testConstructor() {
        final CMakeLinking linking = new CMakeLinking();
        assertNotNull(linking);
    }

    @Test
    void testGetOptions() {
        final CMakeLinking linking = new CMakeLinking();
        Collection<String> options = linking.getOptions();
        assertNotNull(options);
        assertTrue(options.isEmpty());
    }

    @Test
    void testOption() {
        final CMakeLinking linking = new CMakeLinking();
        linking.option("-Wall");
        assertEquals(1, linking.getOptions().size());
        assertTrue(linking.getOptions().contains("-Wall"));
    }

    @Test
    void testOptionsVarargs() {
        final CMakeLinking linking = new CMakeLinking();
        linking.options("-Wall", "-Wextra");
        assertEquals(2, linking.getOptions().size());
        assertTrue(linking.getOptions().contains("-Wall"));
        assertTrue(linking.getOptions().contains("-Wextra"));
    }

    @Test
    void testOptionsCollection() {
        final CMakeLinking linking = new CMakeLinking();
        linking.options(Arrays.asList("-Wall", "-Wextra"));
        assertEquals(2, linking.getOptions().size());
        assertTrue(linking.getOptions().contains("-Wall"));
        assertTrue(linking.getOptions().contains("-Wextra"));
    }

    @Test
    void testGetDependencies() {
        final CMakeLinking linking = new CMakeLinking();
        Collection<CMakeDependencies> dependencies = linking.getDependencies();
        assertNotNull(dependencies);
        assertTrue(dependencies.isEmpty());
    }

    @Test
    void testDependencyCharSequence() {
        final CMakeLinking linking = new CMakeLinking();
        final CMakeDependencies dep = linking.dependency("mylib");
        assertNotNull(dep);
        assertEquals(1, linking.getDependencies().size());
        assertTrue(linking.getDependencies().contains(dep));
    }

    @Test
    void testDependencyCMakeDependencies() {
        final CMakeLinking linking = new CMakeLinking();
        final CMakeDependencies dep = new CMakeDependencies("mylib");
        linking.dependency(dep);
        assertEquals(1, linking.getDependencies().size());
        assertTrue(linking.getDependencies().contains(dep));
    }

    @Test
    void testDependenciesVarargs() {
        final CMakeLinking linking = new CMakeLinking();
        final CMakeDependencies deps = linking.dependencies("lib1", "lib2");
        assertNotNull(deps);
        assertEquals(1, linking.getDependencies().size());
        assertTrue(linking.getDependencies().contains(deps));
    }

    @Test
    void testDependenciesCollection() {
        final CMakeLinking linking = new CMakeLinking();
        final CMakeDependencies dep1 = new CMakeDependencies("lib1");
        final CMakeDependencies dep2 = new CMakeDependencies("lib2");
        linking.dependencies(Arrays.asList(dep1, dep2));
        assertEquals(2, linking.getDependencies().size());
        assertTrue(linking.getDependencies().contains(dep1));
        assertTrue(linking.getDependencies().contains(dep2));
    }
}
