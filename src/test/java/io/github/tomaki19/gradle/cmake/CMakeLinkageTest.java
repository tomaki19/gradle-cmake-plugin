/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkage;

class CMakeLinkageTest {

    @Test
    void testLinkageValues() {
        assertNotNull(CMakeLinkage.STATIC);
        assertNotNull(CMakeLinkage.SHARED);
        assertNotNull(CMakeLinkage.INTERFACE);
    }

    @Test
    void testLinkageToString() {
        assertEquals("static", CMakeLinkage.STATIC.toString());
        assertEquals("shared", CMakeLinkage.SHARED.toString());
        assertEquals("interface", CMakeLinkage.INTERFACE.toString());
    }
}