/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakePluginTest {

  @Test
  void load() {
    final Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(CMakePlugin.class);
    assertNotNull(project.getPlugins().findPlugin("ch.tomaki.gradle-cmake-plugin"));
  }

  @Test
  void extension() {
    final Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(CMakePlugin.class);
    assertNotNull(project.getExtensions().getByName("cmake"));
  }

}
