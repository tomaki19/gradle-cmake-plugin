/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeExtensionTest {

    @Test
    void testExtensionValues() {
        assertEquals("cmake", CMakeExtension.NAME);
    }

    @Test
    void testConstructor() {
        // Test constructor with mock data
        Map<String, Map<io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto, org.gradle.api.Action<io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto>>> customTasks = Collections
                .emptyMap();
        CMakeExtension extension = new CMakeExtension(customTasks) {
            @Override
            public org.gradle.api.NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain> getToolchains() {
                return null;
            }

            @Override
            public org.gradle.api.NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakePackage> getPackages() {
                return null;
            }

            @Override
            public org.gradle.api.NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary> getLibraries() {
                return null;
            }

            @Override
            public org.gradle.api.NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication> getApplications() {
                return null;
            }

            @Override
            public org.gradle.api.NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeTest> getTests() {
                return null;
            }
        };

        assertNotNull(extension);
    }
}