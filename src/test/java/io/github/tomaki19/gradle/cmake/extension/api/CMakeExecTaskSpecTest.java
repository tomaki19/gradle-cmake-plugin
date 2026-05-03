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
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    assertEquals("myTask", spec.getName());
  }

  @Test
  void testCreate_nullName_throws() {
    assertThrows(CMakeApiException.class, () -> CMakeExecTaskSpec.Init.create(Map.of(), null));
  }

  @Test
  void testCreate_blankName_throws() {
    assertThrows(CMakeApiException.class, () -> CMakeExecTaskSpec.Init.create(Map.of(), "  "));
  }

  @Test
  void testCreate_emptyName_throws() {
    assertThrows(CMakeApiException.class, () -> CMakeExecTaskSpec.Init.create(Map.of(), ""));
  }

  @Test
  void testCreate_valid() {
    CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
  }

  @Test
  void testEquals_sameObject() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    assertTrue(spec.equals(spec));
  }

  @Test
  void testEquals_notInstanceOf() {
    final CMakeExecTaskSpec spec = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    assertFalse(spec.equals("not an exec spec"));
  }

  @Test
  void testEquals_sameName() {
    final CMakeExecTaskSpec spec1 = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    final CMakeExecTaskSpec spec2 = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    assertEquals(spec1, spec2);
  }

  @Test
  void testEquals_differentName() {
    final CMakeExecTaskSpec spec1 = CMakeExecTaskSpec.Init.create(Map.of(), "taskA");
    final CMakeExecTaskSpec spec2 = CMakeExecTaskSpec.Init.create(Map.of(), "taskB");
    assertNotEquals(spec1, spec2);
  }

  @Test
  void testHashCode_equal() {
    final CMakeExecTaskSpec spec1 = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    final CMakeExecTaskSpec spec2 = CMakeExecTaskSpec.Init.create(Map.of(), "myTask");
    assertEquals(spec1.hashCode(), spec2.hashCode());
  }
}
