/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;

import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeBinaryTest {

    @Test
    void testConstructor() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeBinary binary = new CMakeBinary(project.getObjects()) {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeCompile getPrivateCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPrivateLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public org.gradle.api.provider.Property<Boolean> getStripDebug() {
                return mock(org.gradle.api.provider.Property.class);
            }
        };
        assertNotNull(binary);
    }

    @Test
    void testOutputName() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeBinary binary = new CMakeBinary(project.getObjects()) {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeCompile getPrivateCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPrivateLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public org.gradle.api.provider.Property<Boolean> getStripDebug() {
                return mock(org.gradle.api.provider.Property.class);
            }
        };

        // Test default value
        assertFalse(binary.getOutputName().isPresent());

        // Test setting value
        binary.setOutputName("testOutput");
        assertTrue(binary.getOutputName().isPresent());
        assertEquals("testOutput", binary.getOutputName().get());
    }

    @Test
    void testToolchains() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeBinary binary = new CMakeBinary(project.getObjects()) {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeCompile getPrivateCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPrivateLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public org.gradle.api.provider.Property<Boolean> getStripDebug() {
                return mock(org.gradle.api.provider.Property.class);
            }
        };

        // Test default value
        Collection<String> toolchains = binary.getToolchains();
        assertNotNull(toolchains);
        assertTrue(toolchains.isEmpty());

        // Test adding toolchains
        binary.toolchains("toolchain1", "toolchain2");
        assertEquals(2, binary.getToolchains().size());

        // Test with collection
        binary.toolchains(Arrays.asList("toolchain3", "toolchain4"));
        assertEquals(4, binary.getToolchains().size());
    }

    @Test
    void testHeaders() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeBinary binary = new CMakeBinary(project.getObjects()) {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeCompile getPrivateCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPrivateLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public org.gradle.api.provider.Property<Boolean> getStripDebug() {
                return mock(org.gradle.api.provider.Property.class);
            }
        };

        SourceDirectorySet headers = binary.getHeaders();
        assertNotNull(headers);
    }

    @Test
    void testSources() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeBinary binary = new CMakeBinary(project.getObjects()) {

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeCompile getPrivateCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPrivateLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public org.gradle.api.provider.Property<Boolean> getStripDebug() {
                return mock(org.gradle.api.provider.Property.class);
            }
        };

        SourceDirectorySet sources = binary.getSources();
        assertNotNull(sources);
    }
}