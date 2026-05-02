/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CMakeCustomExecProtoTest {

  @Test
  void testConstructor() {
    final CMakeCustomExecProto proto = new CMakeCustomExecProto("myTask", t -> {
    });
    assertNotNull(proto);
    assertEquals("myTask", proto.getName());
    assertNotNull(proto.getAction());
  }

  @Test
  void testHashCode_nonNullName() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto("myTask", t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto("myTask", t -> {
    });
    assertEquals(proto1.hashCode(), proto2.hashCode());
  }

  @Test
  void testHashCode_nullName() {
    final CMakeCustomExecProto proto = new CMakeCustomExecProto(null, t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto(null, t -> {
    });
    assertEquals(proto.hashCode(), proto2.hashCode());
  }

  @Test
  void testHashCode_nullVsNonNull() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto(null, t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto("myTask", t -> {
    });
    assertNotEquals(proto1.hashCode(), proto2.hashCode());
  }

  @Test
  void testEquals_sameObject() {
    final CMakeCustomExecProto proto = new CMakeCustomExecProto("myTask", t -> {
    });
    assertTrue(proto.equals(proto));
  }

  @Test
  void testEquals_notCMakeCustomExecProto() {
    final CMakeCustomExecProto proto = new CMakeCustomExecProto("myTask", t -> {
    });
    assertFalse(proto.equals("not a proto"));
  }

  @Test
  void testEquals_sameName() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto("myTask", t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto("myTask", t -> {
    });
    assertEquals(proto1, proto2);
  }

  @Test
  void testEquals_differentName() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto("taskA", t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto("taskB", t -> {
    });
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEquals_bothNullName() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto(null, t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto(null, t -> {
    });
    assertEquals(proto1, proto2);
  }

  @Test
  void testEquals_nullNameThisNotOther() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto(null, t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto("myTask", t -> {
    });
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEquals_nonNullNameOtherNull() {
    final CMakeCustomExecProto proto1 = new CMakeCustomExecProto("myTask", t -> {
    });
    final CMakeCustomExecProto proto2 = new CMakeCustomExecProto(null, t -> {
    });
    assertNotEquals(proto1, proto2);
  }
}
