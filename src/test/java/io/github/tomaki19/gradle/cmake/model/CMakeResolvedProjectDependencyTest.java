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
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertNotNull(dependency);
    assertEquals("test-lib", dependency.getName());
  }

  @Test
  void testGetters() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertNotNull(dependency);
    assertEquals("test-project", dependency.getProjectName());
    assertEquals(CMakeLinkVariant.STATIC, dependency.getLinkVariant());
  }

  @Test
  void testGetProject() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertEquals("test-project", dependency.getProjectName());
    assertEquals(CMakeLinkVariant.STATIC, dependency.getLinkVariant());
  }

  @Test
  void testHashCode() {
    final Project project1 = ProjectBuilder.builder().withName("test-project").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project1, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project2, false);

    assertEquals(dependency1.hashCode(), dependency2.hashCode());
  }

  @Test
  void testEquals() {
    final Project project1 = ProjectBuilder.builder().withName("test-project").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project1, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project2, false);

    assertEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentProject() {
    final Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project1, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project2, false);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentName() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib1",
        CMakeLinkVariant.STATIC, project, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib2",
        CMakeLinkVariant.STATIC, project, false);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithDifferentLinkage() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.SHARED, project, false);

    assertNotEquals(dependency1, dependency2);
  }

  @Test
  void testEqualsWithNull() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertFalse(dependency.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertFalse(dependency.equals("not a dependency"));
  }

  @Test
  void testCompareTo() {
    final Project project1 = ProjectBuilder.builder().withName("test-project1").build();
    final Project project2 = ProjectBuilder.builder().withName("test-project2").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project1, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project2, false);

    // "test-project1" < "test-project2" alphabetically
    assertTrue(dependency1.compareTo(dependency2) < 0);
  }

  @Test
  void testCompareToSame() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    final CMakeResolvedProjectDependency dependency2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertEquals(0, dependency1.compareTo(dependency2));
  }

  @Test
  void testIsRemote() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency localDep = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertFalse(localDep.isRemote());

    final CMakeResolvedProjectDependency remoteDep = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, true);
    assertTrue(remoteDep.isRemote());
  }

  @Test
  void testEqualsWithProject() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertTrue(dependency.equals(project));
  }

  @Test
  void testEqualsWithDifferentProjectObject() {
    final Project project1 = ProjectBuilder.builder().withName("test-project").build();
    final Project project2 = ProjectBuilder.builder().withName("other-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project1, false);

    assertFalse(dependency.equals(project2));
  }

  @Test
  void testEqualsWithNullProject() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertFalse(dependency.equals((Project) null));
  }

  @Test
  void testEquals_sameObject() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertTrue(dependency.equals(dependency));
  }

  @Test
  void testHashCode_nullLinkType() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dependency = new CMakeResolvedProjectDependency("test-lib",
        null, project, false);
    assertEquals(dependency.hashCode(), dependency.hashCode());
  }

  @Test
  void testEquals_nullLinkType_bothNull() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dep1 = new CMakeResolvedProjectDependency("test-lib", null, project, false);
    final CMakeResolvedProjectDependency dep2 = new CMakeResolvedProjectDependency("test-lib", null, project, false);
    assertEquals(dep1, dep2);
  }

  @Test
  void testEquals_nullLinkType_otherNotNull() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();
    final CMakeResolvedProjectDependency dep1 = new CMakeResolvedProjectDependency("test-lib", null, project, false);
    final CMakeResolvedProjectDependency dep2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);
    assertNotEquals(dep1, dep2);
  }

  @Test
  void testCompareToWithDifferentLinkVariant() {
    final Project project = ProjectBuilder.builder().withName("test-project").build();

    final CMakeResolvedProjectDependency dep1 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.SHARED, project, false);
    final CMakeResolvedProjectDependency dep2 = new CMakeResolvedProjectDependency("test-lib",
        CMakeLinkVariant.STATIC, project, false);

    assertNotEquals(0, dep1.compareTo(dep2));
  }
}
