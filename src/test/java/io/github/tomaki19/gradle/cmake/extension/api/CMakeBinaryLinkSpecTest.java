/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeLinkVariant;
import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

class CMakeBinaryLinkSpecTest {

  @Test
  void testConstructor() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    assertNotNull(deps);
  }

  @Test
  void testGetNames() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "lib1", "lib2");
    assertEquals(2, deps.getComponents().size());
  }

  @Test
  void testFrom() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.PROJECT, "myproject"), "mylib");
    assertEquals("myproject", deps.getProject());
  }

  @Test
  void testGetLinkage() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "static"), "mylib");
    assertEquals("static", deps.getLinkVariant().toLowerCase());
  }

  @Test
  void testHashCode() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    assertEquals(deps1.hashCode(), deps2.hashCode());
  }

  @Test
  void testEquals() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    assertEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentNames() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "lib1");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "lib2");
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentFrom() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.PROJECT, "project1"), "mylib");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.PROJECT, "project2"), "mylib");
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithNull() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    assertFalse(deps.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    CMakeBinaryLinkSpec deps = TestCMakeBinaryLinkSpec.Init.create(Map.of(), "mylib");
    assertFalse(deps.equals("not a dependency"));
  }

  @Test
  void testEqualsWithDifferentLinkVariant() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "static"), "mylib");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.LINK_VARIANT, "shared"), "mylib");
    assertNotEquals(deps1, deps2);
  }

  @Test
  void testEqualsWithDifferentVisibility() {
    CMakeBinaryLinkSpec deps1 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.VISIBILITY, "PUBLIC"), "mylib");
    CMakeBinaryLinkSpec deps2 = TestCMakeBinaryLinkSpec.Init.create(
        Map.of(CMakeBinaryLinkSpec.VISIBILITY, "PRIVATE"), "mylib");
    assertNotEquals(deps1, deps2);
  }

  @Test
  void binaryLinkSpec_initConstructor() {
    assertNotNull(new CMakeBinaryLinkSpec.Init() {});
  }

  @Test
  void buildSpec_initConstructor() {
    assertNotNull(new CMakeBuildSpec.Init());
  }

  @Test
  void executableLinkSpec_initConstructor() {
    assertNotNull(new CMakeExecutableLinkSpec.Init());
  }

  @Test
  void libraryLinkSpec_initConstructor() {
    assertNotNull(new CMakeLibraryLinkSpec.Init());
  }

  @Test
  void cmakeApiException_messageAndCause() {
    final Throwable cause = new RuntimeException("root cause");
    final CMakeApiException ex = new CMakeApiException("test message", cause);
    assertEquals("test message", ex.getMessage());
    assertEquals(cause, ex.getCause());
  }

  private static class TestCMakeBinaryLinkSpec extends CMakeBinaryLinkSpec {

    protected TestCMakeBinaryLinkSpec(Set<String> components, String project, CMakeLinkVariant linkVariant,
        CMakeVisibility visibility) {
      super(components, project, linkVariant, visibility);
    }

    public static class Init extends CMakeBinaryLinkSpec.Init {

      public static TestCMakeBinaryLinkSpec create(final Map<String, Object> entries,
          final String... components) throws CMakeApiException {
        validateContentTypes(entries);
        return new TestCMakeBinaryLinkSpec(Arrays.asList(components).stream().map(Object::toString)
            .collect(Collectors.toSet()),
            entries.containsKey(PROJECT) ? ((CharSequence) entries.get(PROJECT)).toString() : null,
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
