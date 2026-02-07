/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.files;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CMakeFileConventionsTest {

    @Test
    void testConstants() {
        assertEquals("cmake/config", CMakeFileConventions.CMAKE_CONFIG_PATH);
        assertEquals("cmake/export", CMakeFileConventions.CMAKE_EXPORT_PATH);
        assertEquals("cmake/install", CMakeFileConventions.CMAKE_INSTALL_PATH);
    }

    @Test
    void testCmakeConfigName() {
        assertEquals("myproject-mytoolchain", CMakeFileConventions.cmakeConfigName("MyProject", "MyToolchain"));
    }

    @Test
    void testBuildTargetWithLinkage() {
        assertEquals("mytarget-mytoolchain-static-debug", CMakeFileConventions.buildTarget("MyTarget", "MyToolchain", "Static", "Debug"));
    }

    @Test
    void testBuildTargetWithoutLinkage() {
        assertEquals("mytarget-mytoolchain-debug", CMakeFileConventions.buildTarget("MyTarget", "MyToolchain", "Debug"));
    }
}
