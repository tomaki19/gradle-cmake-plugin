/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;

class CMakeExtensionTest {

    @Test
    void testExtensionValues() {
        assertEquals("cmake", CMakeExtension.NAME);
    }

    @Test
    void testConstructor() {
        // Test constructor with mock data
        Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks = Collections
                .emptyMap();
        CMakeExtension extension = new CMakeExtension(customTasks) {
            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain> getToolchains() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakePackage> getPackages() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary> getLibraries() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication> getApplications() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeTest> getTests() {
                return null;
            }
        };

        assertNotNull(extension);
    }

    @Test
    void testRegisterWithSingleToolchain() {
        Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks = Collections
                .emptyMap();
        CMakeExtension extension = new CMakeExtension(customTasks) {
            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain> getToolchains() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakePackage> getPackages() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary> getLibraries() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication> getApplications() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeTest> getTests() {
                return null;
            }
        };

        // Test register with single toolchain - this should throw NPE
        // since getToolchains() returns null
        Action<CMakeCustomTaskProto> action = (proto) -> {};
        assertThrows(NullPointerException.class, () -> extension.register("test-task", action));
    }

    @Test
    void testRegisterWithToolchainCollection() {
        Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks = Collections
                .emptyMap();
        CMakeExtension extension = new CMakeExtension(customTasks) {
            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain> getToolchains() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakePackage> getPackages() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary> getLibraries() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication> getApplications() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeTest> getTests() {
                return null;
            }
        };

        // Test register with toolchain collection - this should throw NPE
        // since getToolchains() returns null
        Action<CMakeCustomTaskProto> action = (proto) -> {};
        assertThrows(NullPointerException.class, () -> extension.register("test-task", Collections.singletonList("toolchain1"), action));
    }

    @Test
    void testRegisterWithToolchainAndBuildConfigCollection() {
        Map<String, Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>>> customTasks = Collections
                .emptyMap();
        CMakeExtension extension = new CMakeExtension(customTasks) {
            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain> getToolchains() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakePackage> getPackages() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary> getLibraries() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication> getApplications() {
                return null;
            }

            @Override
            public NamedDomainObjectContainer<io.github.tomaki19.gradle.cmake.extension.api.CMakeTest> getTests() {
                return null;
            }
        };

        // Test register with toolchain and build config collection - this should throw NPE
        // since getToolchains() returns null
        Action<CMakeCustomTaskProto> action = (proto) -> {};
        assertThrows(NullPointerException.class, () -> extension.register("test-task", Collections.singletonList("toolchain1"), Collections.singletonList("debug"), action));
    }
}
