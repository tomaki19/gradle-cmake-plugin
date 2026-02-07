/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class CMakeCompileTest {

    @Test
    void testConstructor() {
        final CMakeCompile compile = new CMakeCompile();
        assertNotNull(compile);
    }

    @Test
    void testGetDefines() {
        final CMakeCompile compile = new CMakeCompile();
        Collection<String> defines = compile.getDefines();
        assertNotNull(defines);
        assertTrue(defines.isEmpty());
    }

    @Test
    void testDefine() {
        final CMakeCompile compile = new CMakeCompile();
        compile.define("DEBUG");
        assertEquals(1, compile.getDefines().size());
        assertTrue(compile.getDefines().contains("DEBUG"));
    }

    @Test
    void testDefinesVarargs() {
        final CMakeCompile compile = new CMakeCompile();
        compile.defines("DEBUG", "VERBOSE");
        assertEquals(2, compile.getDefines().size());
        assertTrue(compile.getDefines().contains("DEBUG"));
        assertTrue(compile.getDefines().contains("VERBOSE"));
    }

    @Test
    void testGetOptions() {
        final CMakeCompile compile = new CMakeCompile();
        Collection<String> options = compile.getOptions();
        assertNotNull(options);
        assertTrue(options.isEmpty());
    }

    @Test
    void testOption() {
        final CMakeCompile compile = new CMakeCompile();
        compile.option("-Wall");
        assertEquals(1, compile.getOptions().size());
        assertTrue(compile.getOptions().contains("-Wall"));
    }

    @Test
    void testOptionsVarargs() {
        final CMakeCompile compile = new CMakeCompile();
        compile.options("-Wall", "-Wextra");
        assertEquals(2, compile.getOptions().size());
        assertTrue(compile.getOptions().contains("-Wall"));
        assertTrue(compile.getOptions().contains("-Wextra"));
    }
}
