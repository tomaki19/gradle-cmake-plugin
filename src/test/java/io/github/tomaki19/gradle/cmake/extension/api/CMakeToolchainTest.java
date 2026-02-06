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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.gradle.internal.os.OperatingSystem;
import org.junit.jupiter.api.Test;

class CMakeToolchainTest {

    @Test
    void testConstructor() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };
        assertNotNull(toolchain);
    }

    @Test
    void testOperatingSystem() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        assertEquals(OperatingSystem.current(), toolchain.getOperatingSystem());

        // Test setting value
        toolchain.setOperatingSystem(OperatingSystem.LINUX);
        assertEquals(OperatingSystem.LINUX, toolchain.getOperatingSystem());
    }

    @Test
    void testGenerator() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        assertFalse(toolchain.getGenerator().isPresent());

        // Test setting value
        toolchain.setGenerator("testGenerator");
        assertTrue(toolchain.getGenerator().isPresent());
        assertEquals("testGenerator", toolchain.getGenerator().get());
    }

    @Test
    void testBuildConfigs() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        Collection<String> buildConfigs = toolchain.getBuildConfigs();
        assertNotNull(buildConfigs);
        assertFalse(buildConfigs.isEmpty());
        assertEquals(2, buildConfigs.size());

        // Test setting value with collection
        toolchain.setBuildConfigs(Arrays.asList("debug", "release", "custom"));
        assertEquals(3, toolchain.getBuildConfigs().size());

        // Test with varargs
        toolchain.buildConfigs("test1", "test2");
        assertEquals(2, toolchain.getBuildConfigs().size());
    }

    @Test
    void testEnvironment() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        Map<String, String> environment = toolchain.getEnvironment();
        assertNotNull(environment);
        assertTrue(environment.isEmpty());

        // Test setting value
        toolchain.setEnvironment(Collections.singletonMap("key", "value"));
        assertFalse(toolchain.getEnvironment().isEmpty());
        assertEquals(1, toolchain.getEnvironment().size());
    }

    @Test
    void testEnvironmentFile() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        assertFalse(toolchain.getEnvironmentFile().isPresent());

        // Test setting value
        File testFile = new File("test.txt");
        toolchain.setEnvironmentFile(testFile);
        assertTrue(toolchain.getEnvironmentFile().isPresent());
        assertEquals(testFile, toolchain.getEnvironmentFile().get());
    }

    @Test
    void testToolchainFile() {
        final CMakeToolchain toolchain = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test default value
        assertFalse(toolchain.getToolchainFile().isPresent());

        // Test setting value
        File testFile = new File("test.txt");
        toolchain.setToolchainFile(testFile);
        assertTrue(toolchain.getToolchainFile().isPresent());
        assertEquals(testFile, toolchain.getToolchainFile().get());
    }

    @Test
    void testCompareTo() {
        final CMakeToolchain toolchain1 = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test1";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        final CMakeToolchain toolchain2 = new CMakeToolchain() {
            @Override
            public String getName() {
                return "test2";
            }

            @Override
            public CMakeBinaries getBinaries() {
                return mock(CMakeBinaries.class);
            }

            @Override
            public CMakeLibraries getLibraries() {
                return mock(CMakeLibraries.class);
            }

            @Override
            public CMakeApplications getApplications() {
                return mock(CMakeApplications.class);
            }

            @Override
            public CMakeTests getTests() {
                return mock(CMakeTests.class);
            }
        };

        // Test comparison
        int result = toolchain1.compareTo(toolchain2);
        assertTrue(result < 0);
    }
}