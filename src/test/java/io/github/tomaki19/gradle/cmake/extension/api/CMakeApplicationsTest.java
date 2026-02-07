/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.gradle.api.provider.Property;
import org.junit.jupiter.api.Test;

class CMakeApplicationsTest {

    @Test
    void testConstructor() {
        final CMakeApplications applications = new CMakeApplications() {
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
        assertNotNull(applications);
    }
}
