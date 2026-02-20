/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

class CMakeResolverTest {

    @Test
    void testResolverCreation() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        final CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);
    }

    @Test
    void testProcessWithEmptyCollections() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        final CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);

        final Set<CMakeLibrary> libraries = Set.of();
        final Set<CMakeApplication> applications = Set.of();
        final Set<CMakeTest> tests = Set.of();

        final Collection<?> results = resolver.process(libraries, applications, tests);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    void testProcessWithNullToolchains() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = null;

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testProcessWithNullPackages() {
        final Project project = mock(Project.class);
        final Set<CMakePackage> packages = null;
        final Set<CMakeToolchain> toolchains = Set.of();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testProcessWithNullProject() {
        final Project project = null;
        final Set<CMakePackage> packages = Set.of();
        final Set<CMakeToolchain> toolchains = Set.of();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullProject() {
        Project project = null;
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullPackages() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = null;
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorWithNullToolchains() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = null;

        assertThrows(NullPointerException.class, () -> {
            new CMakeResolver(project, packages, toolchains);
        });
    }

    @Test
    void testConstructorValid() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);
    }

    @Test
    void testProcessEmpty() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();
        Set<CMakeToolchain> toolchains = Collections.emptySet();
        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);

        // This should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessWithToolchain() {
        Project project = ProjectBuilder.builder().build();
        Set<CMakePackage> packages = Collections.emptySet();

        // Create a mock toolchain
        CMakeToolchain toolchain = mock(CMakeToolchain.class);
        when(toolchain.getName()).thenReturn("test-toolchain");

        Set<CMakeToolchain> toolchains = Set.of(toolchain);
        Set<CMakeLibrary> libraries = Collections.emptySet();
        Set<CMakeApplication> applications = Collections.emptySet();
        Set<CMakeTest> tests = Collections.emptySet();

        CMakeResolver resolver = new CMakeResolver(project, packages, toolchains);
        assertNotNull(resolver);

        // This should not throw any exception
        var result = resolver.process(libraries, applications, tests);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
