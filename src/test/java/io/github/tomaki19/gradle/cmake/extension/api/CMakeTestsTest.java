/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.gradle.api.provider.Property;
import org.junit.jupiter.api.Test;

class CMakeTestsTest {

    @Test
    void testConstructor() {
        final CMakeTests tests = new CMakeTests() {
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
        assertNotNull(tests);
    }

    @Test
    void testGetTestResultsXmlOutput() {
        final CMakeTests tests = new CMakeTests() {
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
        assertTrue(tests.getTestResultsXmlOutput().isEmpty());
    }

    @Test
    void testSetTestResultsXmlOutput() {
        final CMakeTests tests = new CMakeTests() {
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
        tests.setTestResultsXmlOutput(true);
        assertTrue(tests.getTestResultsXmlOutput().isPresent());
        assertEquals(true, tests.getTestResultsXmlOutput().get());
    }
}
