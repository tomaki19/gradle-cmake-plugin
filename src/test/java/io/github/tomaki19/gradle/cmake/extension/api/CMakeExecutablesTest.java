/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class CMakeExecutablesTest {

  @Test
  void testGetCompiling() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExecutables executables = new CMakeExecutables() {
      @Override
      public CMakeExecutableCompiling getCompiling() {
        return new CMakeExecutableCompiling();
      }

      @Override
      public CMakeExecutableLinking getLinking() {
        return new CMakeExecutableLinking();
      }
    };
    assertNotNull(executables.getCompiling());
  }

  @Test
  void testGetLinking() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExecutables executables = new CMakeExecutables() {
      @Override
      public CMakeExecutableCompiling getCompiling() {
        return new CMakeExecutableCompiling();
      }

      @Override
      public CMakeExecutableLinking getLinking() {
        return new CMakeExecutableLinking();
      }
    };
    assertNotNull(executables.getLinking());
  }

  @Test
  void testCompilingAction() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExecutables executables = new CMakeExecutables() {
      @Override
      public CMakeExecutableCompiling getCompiling() {
        return new CMakeExecutableCompiling();
      }

      @Override
      public CMakeExecutableLinking getLinking() {
        return new CMakeExecutableLinking();
      }
    };
    executables.compiling(compiling -> assertNotNull(compiling));
  }

  @Test
  void testLinkingAction() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExecutables executables = new CMakeExecutables() {
      @Override
      public CMakeExecutableCompiling getCompiling() {
        return new CMakeExecutableCompiling();
      }

      @Override
      public CMakeExecutableLinking getLinking() {
        return new CMakeExecutableLinking();
      }
    };
    executables.linking(linking -> assertNotNull(linking));
  }
}
