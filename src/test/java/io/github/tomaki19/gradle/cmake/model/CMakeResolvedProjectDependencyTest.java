/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeResolvedProjectDependencyTest {

  @Test
  void testConstructor() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
  }

  @Test
  void testGetters() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
    assertEquals(CMakeLinkVariant.STATIC, dependency.getLinkType());
  }

  @Test
  void testGetProject() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);
    assertEquals("test-project", dependency.getProjectName());
    assertEquals(CMakeLinkVariant.STATIC, dependency.getLinkType());
  }

  @Test
  void testHashCode() {
    final Project project1 = ProjectBuilder.builder().withName("test-project").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project1,
        CMakeLinkVariant.STATIC);

    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project2,
        CMakeLinkVariant.STATIC);

    assertEquals(dependency1.hashCode(), dependency2.hashCode());
  }

  @Test
  void testEquals() {
    final Project project1 = ProjectBuilder.builder().withName("test-project").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project1,
        CMakeLinkVariant.STATIC);

    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project2,
        CMakeLinkVariant.STATIC);

    assertEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentProject() {
    final Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project1,
        CMakeLinkVariant.STATIC);

    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project2,
        CMakeLinkVariant.STATIC);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentName() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib1", project,
        CMakeLinkVariant.STATIC);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib2", project,
        CMakeLinkVariant.STATIC);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentLinkage() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.SHARED);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithNull() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);

    assertFalse(dependency.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);

    assertFalse(dependency.equals("not a dependency"));
  }

  @Test
  void testCompareTo() {
    final Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project1,
        CMakeLinkVariant.STATIC);

    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project2,
        CMakeLinkVariant.STATIC);

    // "test-project1" < "test-project2" alphabetically
    assertTrue(dependency1.compareTo(dependency2) < 0);
  }

  @Test
  void testCompareToSame() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);

    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", project,
        CMakeLinkVariant.STATIC);

    assertEquals(0, dependency1.compareTo(dependency2));
  }
}
