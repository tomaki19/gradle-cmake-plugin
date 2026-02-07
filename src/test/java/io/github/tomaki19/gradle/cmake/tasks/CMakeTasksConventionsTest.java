/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CMakeTasksConventionsTest {

    @Test
    void testAssembleListsTaskName() {
        assertEquals("assemble-cmake-lists", CMakeTasksConventions.assembleListsTaskName());
    }

    @Test
    void testAssembleConfigTaskNameToolchain() {
        assertEquals("assemble-mytoolchain-config", CMakeTasksConventions.assembleConfigTaskName("mytoolchain"));
    }

    @Test
    void testAssembleConfigTaskNameProjectToolchain() {
        assertEquals(":myproject:assemble-mytoolchain-config", CMakeTasksConventions.assembleConfigTaskName("myproject", "mytoolchain"));
    }

    @Test
    void testCustomExecTaskName() {
        assertEquals("mytask-mytoolchain-debug", CMakeTasksConventions.customExecTaskName("mytask", "mytoolchain", "debug"));
    }

    @Test
    void testConfigureTaskNameProjectToolchainBuildConfig() {
        assertEquals(":myproject:configure-mytoolchain-debug", CMakeTasksConventions.configureTaskName("myproject", "mytoolchain", "debug"));
    }

    @Test
    void testConfigureTaskNameToolchainBuildConfig() {
        assertEquals("configure-mytoolchain-debug", CMakeTasksConventions.configureTaskName("mytoolchain", "debug"));
    }

    @Test
    void testBuildTaskNameProjectTargetToolchainLinkageBuildConfig() {
        assertEquals(":myproject:build-mytarget-mytoolchain-static-debug", CMakeTasksConventions.buildTaskName("myproject", "mytarget", "mytoolchain", "static", "debug"));
    }

    @Test
    void testBuildTaskNameTargetToolchainLinkageBuildConfig() {
        assertEquals("build-mytarget-mytoolchain-static-debug", CMakeTasksConventions.buildTaskName("mytarget", "mytoolchain", "static", "debug"));
    }

    @Test
    void testBuildTaskNameTargetToolchainBuildConfig() {
        assertEquals("build-mytarget-mytoolchain-debug", CMakeTasksConventions.buildTaskName("mytarget", "mytoolchain", "debug"));
    }

    @Test
    void testBuildAllTaskName() {
        assertEquals("build-all-mytoolchain", CMakeTasksConventions.buildAllTaskName("mytoolchain"));
    }

    @Test
    void testCheckTaskNameTargetToolchainLinkageBuildConfig() {
        assertEquals("check-mytarget-mytoolchain-static-debug", CMakeTasksConventions.checkTaskName("mytarget", "mytoolchain", "static", "debug"));
    }

    @Test
    void testCheckTaskNameTargetToolchainBuildConfig() {
        assertEquals("check-mytarget-mytoolchain-debug", CMakeTasksConventions.checkTaskName("mytarget", "mytoolchain", "debug"));
    }

    @Test
    void testCheckAllTaskName() {
        assertEquals("check-all-mytoolchain", CMakeTasksConventions.checkAllTaskName("mytoolchain"));
    }

    @Test
    void testPackageTaskNameTargetToolchainLinkageBuildConfig() {
        assertEquals("package-mytarget-mytoolchain-static-debug", CMakeTasksConventions.packageTaskName("mytarget", "mytoolchain", "static", "debug"));
    }

    @Test
    void testPackageTaskNameTargetToolchainBuildConfig() {
        assertEquals("package-mytarget-mytoolchain-debug", CMakeTasksConventions.packageTaskName("mytarget", "mytoolchain", "debug"));
    }
}
