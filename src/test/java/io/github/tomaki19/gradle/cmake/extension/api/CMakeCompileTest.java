/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class CMakeCompileTest {

    @Test
    void testConstructor() {
        CMakeCompile compile = new CMakeCompile();
        assertNotNull(compile);
    }

    @Test
    void testGetDefines() {
        CMakeCompile compile = new CMakeCompile();
        Collection<String> defines = compile.getDefines();
        assertNotNull(defines);
        assertEquals(0, defines.size());
    }

    @Test
    void testGetOptions() {
        CMakeCompile compile = new CMakeCompile();
        Collection<String> options = compile.getOptions();
        assertNotNull(options);
        assertEquals(0, options.size());
    }

    @Test
    void testDefine() {
        CMakeCompile compile = new CMakeCompile();
        compile.define("DEBUG");
        assertEquals(1, compile.getDefines().size());
        assertEquals("DEBUG", compile.getDefines().iterator().next());
    }

    @Test
    void testDefinesVarargs() {
        CMakeCompile compile = new CMakeCompile();
        compile.defines("DEBUG", "VERBOSE");
        assertEquals(2, compile.getDefines().size());
    }

    @Test
    void testOption() {
        CMakeCompile compile = new CMakeCompile();
        compile.option("-Wall");
        assertEquals(1, compile.getOptions().size());
        assertEquals("-Wall", compile.getOptions().iterator().next());
    }

    @Test
    void testOptionsVarargs() {
        CMakeCompile compile = new CMakeCompile();
        compile.options("-Wall", "-Wextra");
        assertEquals(2, compile.getOptions().size());
    }
}
