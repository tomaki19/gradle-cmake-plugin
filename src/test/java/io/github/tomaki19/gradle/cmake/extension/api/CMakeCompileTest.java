/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

class CMakeCompileTest {

  @Test
  void testGetDefines() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    Collection<CMakeBuildItems> defines = compile.getDefines();
    assertNotNull(defines);
    assertEquals(0, defines.size());
  }

  @Test
  void testGetOptions() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    Collection<CMakeBuildItems> options = compile.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testDefine() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines("DEBUG");
    assertEquals(1, compile.getDefines().size());
    assertEquals(new CMakeBuildItems(CMakeVisibility.PUBLIC, "DEBUG"), compile.getDefines().iterator().next());
  }

  @Test
  void testDefinesVarargs() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines("DEBUG", "VERBOSE");
    assertEquals(1, compile.getDefines().size());
    assertEquals(new CMakeBuildItems(CMakeVisibility.PUBLIC, "DEBUG", "VERBOSE"),
        compile.getDefines().iterator().next());
  }

  @Test
  void testOption() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options("-Wall");
    assertEquals(1, compile.getOptions().size());
    assertEquals(new CMakeBuildItems(CMakeVisibility.PRIVATE, "-Wall"), compile.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options("-Wall", "-Wextra");
    assertEquals(1, compile.getOptions().size());
    assertEquals(new CMakeBuildItems(CMakeVisibility.PRIVATE, "-Wall", "-Wextra"),
        compile.getOptions().iterator().next());
  }
}
