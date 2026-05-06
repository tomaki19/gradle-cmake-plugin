/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.gradle.api.tasks.bundling.Zip;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.tasks.CMakeCustomZip;

class CMakeArchiveTaskSpecTest {

  @Test
  void testGetType_defaultType() {
    final CMakeArchiveTaskSpec spec = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    assertEquals(CMakeCustomZip.class, spec.getType());
  }

  @Test
  void testGetType_overriddenType() {
    final CMakeArchiveTaskSpec spec = CMakeArchiveTaskSpec.Init.create(Map.of("type", Zip.class), Zip.class);
    assertEquals(Zip.class, spec.getType());
  }

  @Test
  void testEquals_sameObject() {
    final CMakeArchiveTaskSpec spec = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    assertTrue(spec.equals(spec));
  }

  @Test
  void testEquals_notInstanceOf() {
    final CMakeArchiveTaskSpec spec = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    assertFalse(spec.equals("not an archive spec"));
  }

  @Test
  void testEquals_differentType() {
    final CMakeArchiveTaskSpec spec1 = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    final CMakeArchiveTaskSpec spec2 = CMakeArchiveTaskSpec.Init.create(Map.of(), Zip.class);
    assertNotEquals(spec1, spec2);
  }

  @Test
  void testEquals_sameType_sameContent() {
    final CMakeArchiveTaskSpec spec1 = CMakeArchiveTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc")), CMakeCustomZip.class);
    final CMakeArchiveTaskSpec spec2 = CMakeArchiveTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc")), CMakeCustomZip.class);
    assertEquals(spec1, spec2);
  }

  @Test
  void testEquals_sameType_differentContent() {
    final CMakeArchiveTaskSpec spec1 = CMakeArchiveTaskSpec.Init.create(
        Map.of("toolchains", Set.of("gcc")), CMakeCustomZip.class);
    final CMakeArchiveTaskSpec spec2 = CMakeArchiveTaskSpec.Init.create(
        Map.of("toolchains", Set.of("clang")), CMakeCustomZip.class);
    assertNotEquals(spec1, spec2);
  }

  @Test
  void testHashCode_equal() {
    final CMakeArchiveTaskSpec spec1 = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    final CMakeArchiveTaskSpec spec2 = CMakeArchiveTaskSpec.Init.create(Map.of(), CMakeCustomZip.class);
    assertEquals(spec1.hashCode(), spec2.hashCode());
  }

  @Test
  void testInit_canBeInstantiated() {
    assertNotNull(new CMakeArchiveTaskSpec.Init());
  }
}
