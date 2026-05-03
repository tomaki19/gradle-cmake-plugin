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

import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeExecTaskSpecTest {

  @Test
  void testGetName() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    assertEquals("myTask", spec.getName());
  }

  @Test
  void testGetName_missing() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertEquals("", spec.getName());
  }

  @Test
  void testValidate_valid() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    spec.validate();
  }

  @Test
  void testValidate_missingName_throws() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of());
    assertThrows(CMakeApiException.class, spec::validate);
  }

  @Test
  void testValidate_blankName_throws() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", "  "));
    assertThrows(CMakeApiException.class, spec::validate);
  }

  @Test
  void testValidate_emptyName_throws() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", ""));
    assertThrows(CMakeApiException.class, spec::validate);
  }

  @Test
  void testEquals_sameObject() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    assertTrue(spec.equals(spec));
  }

  @Test
  void testEquals_notInstanceOf() {
    final CMakeExecTaskSpec spec = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    assertFalse(spec.equals("not an exec spec"));
  }

  @Test
  void testEquals_sameName() {
    final CMakeExecTaskSpec spec1 = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    final CMakeExecTaskSpec spec2 = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    assertEquals(spec1, spec2);
  }

  @Test
  void testEquals_differentName() {
    final CMakeExecTaskSpec spec1 = new CMakeExecTaskSpec(Map.of("name", "taskA"));
    final CMakeExecTaskSpec spec2 = new CMakeExecTaskSpec(Map.of("name", "taskB"));
    assertNotEquals(spec1, spec2);
  }

  @Test
  void testHashCode_equal() {
    final CMakeExecTaskSpec spec1 = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    final CMakeExecTaskSpec spec2 = new CMakeExecTaskSpec(Map.of("name", "myTask"));
    assertEquals(spec1.hashCode(), spec2.hashCode());
  }
}
