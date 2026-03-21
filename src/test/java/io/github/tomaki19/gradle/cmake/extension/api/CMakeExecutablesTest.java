/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CMakeExecutablesTest {

  @Test
  void testGetCompiling() {
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
