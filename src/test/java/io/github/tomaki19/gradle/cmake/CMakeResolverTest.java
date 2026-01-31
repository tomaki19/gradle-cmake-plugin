/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Set;

import org.gradle.api.Project;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.model.CMakeResolver;

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
}