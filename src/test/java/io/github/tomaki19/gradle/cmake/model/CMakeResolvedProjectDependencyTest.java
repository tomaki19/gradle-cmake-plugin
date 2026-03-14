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

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
  }

  @Test
  void testGetters() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
    assertEquals(CMakeLinkVariant.STATIC, dependency.getLinkType());
  }

  @Test
  void testGetProject() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);
    assertNotNull(dependency.getResolvedProject());
    assertEquals("test-project", dependency.getResolvedProject().getName());
  }

  @Test
  void testHashCode() {
    Project project1 = ProjectBuilder.builder().withName("test-project").build();
    Project project2 = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject1 = new CMakeResolvedProject(project1);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject1,
        CMakeLinkVariant.STATIC);

    CMakeResolvedProject resolvedProject2 = new CMakeResolvedProject(project2);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject2,
        CMakeLinkVariant.STATIC);

    assertEquals(dependency1.hashCode(), dependency2.hashCode());
  }

  @Test
  void testEquals() {
    Project project1 = ProjectBuilder.builder().withName("test-project").build();
    Project project2 = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject1 = new CMakeResolvedProject(project1);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject1,
        CMakeLinkVariant.STATIC);

    CMakeResolvedProject resolvedProject2 = new CMakeResolvedProject(project2);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject2,
        CMakeLinkVariant.STATIC);

    assertEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentProject() {
    Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    CMakeResolvedProject resolvedProject1 = new CMakeResolvedProject(project1);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject1,
        CMakeLinkVariant.STATIC);

    CMakeResolvedProject resolvedProject2 = new CMakeResolvedProject(project2);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject2,
        CMakeLinkVariant.STATIC);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentName() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib1", resolvedProject,
        CMakeLinkVariant.STATIC);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib2", resolvedProject,
        CMakeLinkVariant.STATIC);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentLinkage() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.SHARED);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithNull() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);

    assertFalse(dependency.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib", resolvedProject,
        CMakeLinkVariant.STATIC);

    assertFalse(dependency.equals("not a dependency"));
  }

  @Test
  void testCompareTo() {
    Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    CMakeResolvedProject resolvedProject1 = new CMakeResolvedProject(project1);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject1,
        CMakeLinkVariant.STATIC);

    CMakeResolvedProject resolvedProject2 = new CMakeResolvedProject(project2);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject2,
        CMakeLinkVariant.STATIC);

    // "test-project1" < "test-project2" alphabetically
    assertTrue(dependency1.compareTo(dependency2) < 0);
  }

  @Test
  void testCompareToSame() {
    Project project = ProjectBuilder.builder().withName("test-project").build();

    CMakeResolvedProject resolvedProject1 = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib", resolvedProject1,
        CMakeLinkVariant.STATIC);

    CMakeResolvedProject resolvedProject2 = new CMakeResolvedProject(project);
    CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib", resolvedProject2,
        CMakeLinkVariant.STATIC);

    assertEquals(0, dependency1.compareTo(dependency2));
  }
}
