/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeLibraryTest {

    @Test
    void testConstructor() {
        final Project project = ProjectBuilder.builder().build();
        final CMakeLibrary library = new CMakeLibrary(project.getObjects()) {
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

            @Override
            public CMakeCompile getPublicCompile() {
                return mock(CMakeCompile.class);
            }

            @Override
            public CMakeLinking getPublicLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public CMakeLinking getPublicInterfaceLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public CMakeLinking getPublicStaticLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public CMakeLinking getPublicSharedLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public Property<Boolean> getBuildStatic() {
                return mock(Property.class);
            }

            @Override
            public Property<Boolean> getBuildShared() {
                return mock(Property.class);
            }

            @Override
            public CMakeLinking getPrivateInterfaceLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public CMakeLinking getPrivateStaticLinking() {
                return mock(CMakeLinking.class);
            }

            @Override
            public CMakeLinking getPrivateSharedLinking() {
                return mock(CMakeLinking.class);
            }
        };
        assertNotNull(library);
    }
}