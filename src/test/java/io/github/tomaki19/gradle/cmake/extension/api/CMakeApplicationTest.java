/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeApplicationTest {

    @Test
    void testConstructor() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeApplication application = new CMakeApplication(project.getObjects()) {
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
            public Property<Boolean> getStripDebug() {
                return mock(Property.class);
            }
        };
        assertNotNull(application);
    }

    @Test
    void testGetName() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeApplication application = new CMakeApplication(project.getObjects()) {
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
            public Property<Boolean> getStripDebug() {
                return mock(Property.class);
            }
        };
        assertEquals("test", application.getName());
    }

    @Test
    void testGetPrivateCompile() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeApplication application = new CMakeApplication(project.getObjects()) {
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
            public Property<Boolean> getStripDebug() {
                return mock(Property.class);
            }
        };
        assertNotNull(application.getPrivateCompile());
    }

    @Test
    void testGetPrivateLinking() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeApplication application = new CMakeApplication(project.getObjects()) {
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
            public Property<Boolean> getStripDebug() {
                return mock(Property.class);
            }
        };
        assertNotNull(application.getPrivateLinking());
    }

    @Test
    void testGetStripDebug() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeApplication application = new CMakeApplication(project.getObjects()) {
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
            public Property<Boolean> getStripDebug() {
                return mock(Property.class);
            }
        };
        assertNotNull(application.getStripDebug());
    }
}
