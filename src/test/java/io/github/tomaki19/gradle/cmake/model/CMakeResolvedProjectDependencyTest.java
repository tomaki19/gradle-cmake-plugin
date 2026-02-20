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
    Project project = ProjectBuilder.builder().withName("test-project").build();
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
  }

  @Test
  void testGetters() {
    Project project = ProjectBuilder.builder().withName("test-project").build();
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.of(CMakeLinkType.STATIC));
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
    assertEquals("static", dependency.getLinkage());
  }

  @Test
  void testGetProject() {
    Project project = ProjectBuilder.builder().withName("test-project").build();
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());
    assertNotNull(dependency.getProject());
    assertEquals("test-project", dependency.getProject().getName());
  }

  @Test
  void testHashCode() {
    Project project1 = ProjectBuilder.builder().withName("test-project").build();
    Project project2 = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project1, "test-lib",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project2, "test-lib",
        java.util.Optional.empty());

    assertEquals(dependency1.hashCode(), dependency2.hashCode());
  }

  @Test
  void testEquals() {
    Project project1 = ProjectBuilder.builder().withName("test-project").build();
    Project project2 = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project1, "test-lib",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project2, "test-lib",
        java.util.Optional.empty());

    assertEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentProject() {
    Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project1, "test-lib",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project2, "test-lib",
        java.util.Optional.empty());

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentName() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project, "test-lib1",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project, "test-lib2",
        java.util.Optional.empty());

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentLinkage() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.of(CMakeLinkType.STATIC));
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.of(CMakeLinkType.SHARED));

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithNull() {
    Project project = ProjectBuilder.builder().withName("test-project").build();
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());

    assertFalse(dependency.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    Project project = ProjectBuilder.builder().withName("test-project").build();
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());

    assertFalse(dependency.equals("not a dependency"));
  }

  @Test
  void testCompareTo() {
    Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project1, "test-lib",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project2, "test-lib",
        java.util.Optional.empty());

    // "test-project1" < "test-project2" alphabetically
    assertTrue(dependency1.compareTo(dependency2) < 0);
  }

  @Test
  void testCompareToSame() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency(project, "test-lib",
        java.util.Optional.empty());

    assertEquals(0, dependency1.compareTo(dependency2));
  }
}
