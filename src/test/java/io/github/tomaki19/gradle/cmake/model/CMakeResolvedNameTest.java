/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CMakeResolvedNameTest {

    @Test
    void testGetName() {
        final TestCMakeResolvedName name = new TestCMakeResolvedName("test");
        assertEquals("test", name.getName());
    }

    @Test
    void testHashCode() {
        final TestCMakeResolvedName name1 = new TestCMakeResolvedName("test");
        final TestCMakeResolvedName name2 = new TestCMakeResolvedName("test");
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        final TestCMakeResolvedName name = new TestCMakeResolvedName("test");
        assertEquals(name, name);
    }

    @Test
    void testEqualsNull() {
        final TestCMakeResolvedName name = new TestCMakeResolvedName("test");
        assertEquals(false, name.equals(null));
    }

    @Test
    void testEqualsDifferentNames() {
        final TestCMakeResolvedName name1 = new TestCMakeResolvedName("test1");
        final TestCMakeResolvedName name2 = new TestCMakeResolvedName("test2");
        assertEquals(false, name1.equals(name2));
    }

    @Test
    void testEqualsSameNames() {
        final TestCMakeResolvedName name1 = new TestCMakeResolvedName("test");
        final TestCMakeResolvedName name2 = new TestCMakeResolvedName("test");
        assertEquals(true, name1.equals(name2));
    }

    @Test
    void testCompareToDifferentNames() {
        final TestCMakeResolvedName name1 = new TestCMakeResolvedName("aaa");
        final TestCMakeResolvedName name2 = new TestCMakeResolvedName("zzz");
        int result = name1.compareTo(name2);
        assertTrue(result < 0);
    }

    @Test
    void testCompareToSameNames() {
        final TestCMakeResolvedName name1 = new TestCMakeResolvedName("test");
        final TestCMakeResolvedName name2 = new TestCMakeResolvedName("test");
        int result = name1.compareTo(name2);
        assertEquals(0, result);
    }

    private static class TestCMakeResolvedName extends CMakeResolvedName<TestCMakeResolvedName> {
        TestCMakeResolvedName(String name) {
            super(name);
        }
    }
}
