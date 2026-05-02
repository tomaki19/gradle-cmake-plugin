/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.exceptions.CMakeApiException;
import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

class CMakeBinaryLinkSpecTest {

  @Test
  void testConstructor() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("lib1", "lib2"), Map.of());
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"),
        Map.of(CMakeBinaryLinkSpec.PROJECT, "myproject"));
    assertEquals("myproject", deps.getProject());
  }

  @Test
  void testGetLinkage() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"),
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "static"));
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testHashCode() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEquals() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentNames() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(List.of("lib1"), Map.of());
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(List.of("lib2"), Map.of());
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentFrom() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"),
        Map.of(CMakeBinaryLinkSpec.PROJECT, "project1"));
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"),
        Map.of(CMakeBinaryLinkSpec.PROJECT, "project2"));
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertFalse(deps.equals(""));
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(List.of("mylib"), Map.of());
    assertFalse(deps.equals("not a dependency"));
  }

  private static class TestCMakeBinaryLinkSpec extends CMakeBinaryLinkSpec {

    protected TestCMakeBinaryLinkSpec(Set<String> components, String project, CMakeLinkVariant linkVariant,
        CMakeVisibility visibility) {
      super(components, project, linkVariant, visibility);
    }

    public static class Init extends CMakeBinaryLinkSpec.Init {

      public static TestCMakeBinaryLinkSpec create(final Collection<CharSequence> components,
          final Map<String, Object> entries) throws CMakeApiException {
        validateContentTypes(entries);
        return new TestCMakeBinaryLinkSpec(components.stream().map((it) -> it.toString())
            .collect(Collectors.toSet()),
            entries.containsKey(PROJECT) ? ((CharSequence) entries.get(PROJECT)).toString() : "",
            entries.containsKey(LINK_VARIANT)
                ? CMakeLinkVariant.valueOf(((CharSequence) entries.get(LINK_VARIANT)).toString().toUpperCase())
                : CMakeLinkVariant.SHARED,
            entries.containsKey(VISIBILITY)
                ? CMakeVisibility.valueOf(((CharSequence) entries.get(VISIBILITY)).toString().toUpperCase())
                : CMakeVisibility.PUBLIC);
      }
    }
  }
}
