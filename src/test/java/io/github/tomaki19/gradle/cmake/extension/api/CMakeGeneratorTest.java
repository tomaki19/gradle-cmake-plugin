/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CMakeGeneratorTest {

    @Test
    void testCustomGenerator() {
        final CMakeGenerator generator = CMakeGenerator.custom("CustomGenerator");
        assertNotNull(generator);
        assertEquals("CustomGenerator", generator.toString());
    }

    @Test
    void testPredefinedGenerators() {
        assertNotNull(CMakeGenerator.BORLAND_MAKEFILES);
        assertNotNull(CMakeGenerator.MSYS_MAKEFILES);
        assertNotNull(CMakeGenerator.MINGW_MAKEFILE);
        assertNotNull(CMakeGenerator.NMAKE_MAKEFILES);
        assertNotNull(CMakeGenerator.NMAKE_MAKEFILES_JOM);
        assertNotNull(CMakeGenerator.UNIX_MAKEFILES);
        assertNotNull(CMakeGenerator.WATCOM_WMAKE);
        assertNotNull(CMakeGenerator.NINJA);
        assertNotNull(CMakeGenerator.NINJA_MULTI_CONFIG);
        assertNotNull(CMakeGenerator.FASTBUILD);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_6);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_7);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_7_NET_2003);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_8_2005);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_9_2008);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_10_2010);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_11_2012);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_12_2013);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_14_2015);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_15_2017);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_16_2019);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_17_2022);
        assertNotNull(CMakeGenerator.VISUAL_STUDIO_18_2026);
        assertNotNull(CMakeGenerator.GREEN_HILLS_MULTI);
        assertNotNull(CMakeGenerator.XCODE);
        assertNotNull(CMakeGenerator.CODEBLOCKS);
        assertNotNull(CMakeGenerator.CODELITE);
        assertNotNull(CMakeGenerator.ECLIPSE_CDT4);
        assertNotNull(CMakeGenerator.KATE);
        assertNotNull(CMakeGenerator.SUBLIME_TEXT_2);
    }

    @Test
    void testToString() {
        final CMakeGenerator generator = CMakeGenerator.custom("MyGenerator");
        assertEquals("MyGenerator", generator.toString());
    }
}
