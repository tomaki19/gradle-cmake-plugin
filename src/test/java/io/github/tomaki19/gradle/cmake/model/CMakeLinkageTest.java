/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CMakeLinkTypeTest {

    @Test
    void testLinkageValues() {
        assertNotNull(CMakeLinkType.STATIC);
        assertNotNull(CMakeLinkType.SHARED);
        assertNotNull(CMakeLinkType.INTERFACE);
    }

    @Test
    void testLinkageToString() {
        assertEquals("static", CMakeLinkType.STATIC.toString());
        assertEquals("shared", CMakeLinkType.SHARED.toString());
        assertEquals("interface", CMakeLinkType.INTERFACE.toString());
    }
}
