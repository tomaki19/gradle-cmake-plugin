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

import org.gradle.api.tasks.bundling.Zip;
import org.junit.jupiter.api.Test;

class CMakeCustomTaskProtoTest {

  @Test
  void testConstructor() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertNotNull(proto);
    assertEquals(Zip.class, proto.getType());
    assertNotNull(proto.getAction());
  }

  @Test
  void testHashCode_nonNullType() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertEquals(proto.hashCode(), proto.hashCode());
  }

  @Test
  void testHashCode_nullType() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(null, t -> {
    });
    assertEquals(0 * 31 + 1, proto.hashCode() / 31);
  }

  @Test
  void testEquals_sameObject() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertTrue(proto.equals(proto));
  }

  @Test
  void testEquals_null() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertFalse(proto.equals(null));
  }

  @Test
  void testEquals_differentClass() {
    final CMakeCustomTaskProto<Zip> proto = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertFalse(proto.equals("not a proto"));
  }

  @Test
  void testEquals_sameType() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertEquals(proto1, proto2);
  }

  @Test
  void testEquals_differentType() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    final CMakeCustomTaskProto<CMakeCustomZip> proto2 = new CMakeCustomTaskProto<>(CMakeCustomZip.class, t -> {
    });
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEquals_bothNullType() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(null, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(null, t -> {
    });
    assertEquals(proto1, proto2);
  }

  @Test
  void testEquals_nullTypeThisNotNull() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(null, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testEquals_nonNullTypeOtherNull() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(null, t -> {
    });
    assertNotEquals(proto1, proto2);
  }

  @Test
  void testHashCode_consistency() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertEquals(proto1.hashCode(), proto2.hashCode());
  }

  @Test
  void testHashCode_nullVsNonNull() {
    final CMakeCustomTaskProto<Zip> proto1 = new CMakeCustomTaskProto<>(null, t -> {
    });
    final CMakeCustomTaskProto<Zip> proto2 = new CMakeCustomTaskProto<>(Zip.class, t -> {
    });
    assertNotEquals(proto1.hashCode(), proto2.hashCode());
  }
}
