/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CMakeCompileTest {

  @Test
  void testGetDefines() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    Collection<CMakeBuildSpec> defines = compile.getDefines();
    assertNotNull(defines);
    assertEquals(0, defines.size());
  }

  @Test
  void testGetOptions() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    Collection<CMakeBuildSpec> options = compile.getOptions();
    assertNotNull(options);
    assertEquals(0, options.size());
  }

  @Test
  void testDefine() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines(List.<CharSequence>of("DEBUG"), Map.of());
    assertEquals(1, compile.getDefines().size());
    assertEquals(CMakeBuildSpec.Init.create(List.<CharSequence>of("DEBUG"), Map.of()), compile.getDefines().iterator().next());
  }

  @Test
  void testDefinesVarargs() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines(List.<CharSequence>of("DEBUG", "VERBOSE"), Map.of());
    assertEquals(1, compile.getDefines().size());
    assertEquals(CMakeBuildSpec.Init.create(List.<CharSequence>of("DEBUG", "VERBOSE"), Map.of()),
        compile.getDefines().iterator().next());
  }

  @Test
  void testOption() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options(List.<CharSequence>of("-Wall"), Map.of());
    assertEquals(1, compile.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(List.<CharSequence>of("-Wall"), Map.of()), compile.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options(List.<CharSequence>of("-Wall", "-Wextra"), Map.of());
    assertEquals(1, compile.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(List.<CharSequence>of("-Wall", "-Wextra"), Map.of()),
        compile.getOptions().iterator().next());
  }
}
