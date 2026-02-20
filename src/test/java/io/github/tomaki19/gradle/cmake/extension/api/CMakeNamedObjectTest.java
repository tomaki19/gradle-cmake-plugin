/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CMakeNamedObjectTest {

  @Test
  void testHashCode() {
    CMakeNamedObject obj1 = new TestCMakeNamedObject("test");
    CMakeNamedObject obj2 = new TestCMakeNamedObject("test");

    assertEquals(obj1.hashCode(), obj2.hashCode());
  }

  @Test
  void testEqualsSameObject() {
    CMakeNamedObject obj = new TestCMakeNamedObject("test");

    assertEquals(obj, obj);
  }

  @Test
  void testEqualsNull() {
    CMakeNamedObject obj = new TestCMakeNamedObject("test");

    assertFalse(obj.equals(null));
  }

  @Test
  void testEqualsDifferentClass() {
    CMakeNamedObject obj = new TestCMakeNamedObject("test");

    assertFalse(obj.equals("not a named object"));
  }

  @Test
  void testEqualsSameName() {
    CMakeNamedObject obj1 = new TestCMakeNamedObject("test");
    CMakeNamedObject obj2 = new TestCMakeNamedObject("test");

    assertEquals(obj1, obj2);
  }

  @Test
  void testEqualsDifferentName() {
    CMakeNamedObject obj1 = new TestCMakeNamedObject("test1");
    CMakeNamedObject obj2 = new TestCMakeNamedObject("test2");

    assertNotEquals(obj1, obj2);
  }

  @Test
  void testCompareToSameName() {
    CMakeNamedObject obj1 = new TestCMakeNamedObject("test");
    CMakeNamedObject obj2 = new TestCMakeNamedObject("test");

    assertEquals(0, obj1.compareTo(obj2));
  }

  @Test
  void testCompareToDifferentName() {
    CMakeNamedObject obj1 = new TestCMakeNamedObject("test1");
    CMakeNamedObject obj2 = new TestCMakeNamedObject("test2");

    // "test1" < "test2" alphabetically
    assertTrue(obj1.compareTo(obj2) < 0);
  }

  @Test
  void testCompareToNull() {
    CMakeNamedObject obj = new TestCMakeNamedObject("test");

    // compareTo should throw NullPointerException when comparing with null
    assertThrows(NullPointerException.class, () -> obj.compareTo(null));
  }

  private static class TestCMakeNamedObject extends CMakeNamedObject {
    private final String name;

    TestCMakeNamedObject(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }
}
