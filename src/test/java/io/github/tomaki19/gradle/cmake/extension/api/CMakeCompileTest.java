/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.model.CMakeVisibility;

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
    compile.defines(Map.of(), "DEBUG");
    assertEquals(1, compile.getDefines().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "DEBUG"), compile.getDefines().iterator().next());
  }

  @Test
  void testDefinesVarargs() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines(Map.of(), "DEBUG", "VERBOSE");
    assertEquals(1, compile.getDefines().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "DEBUG", "VERBOSE"),
        compile.getDefines().iterator().next());
  }

  @Test
  void testOption() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options(Map.of(), "-Wall");
    assertEquals(1, compile.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-Wall"), compile.getOptions().iterator().next());
  }

  @Test
  void testOptionsVarargs() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options(Map.of(), "-Wall", "-Wextra");
    assertEquals(1, compile.getOptions().size());
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-Wall", "-Wextra"),
        compile.getOptions().iterator().next());
  }

  @Test
  void testOptionsWithoutSpec() {
    CMakeExecutableCompiling compile = new CMakeExecutableCompiling();
    compile.options("-Wall", "-Wextra");
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "-Wall", "-Wextra"), compile.getOptions().iterator().next());
    assertEquals(CMakeVisibility.PUBLIC, compile.getOptions().iterator().next().getVisibility());
  }

  @Test
  void testDefinesWithoutSpec() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines("DEBUG");
    assertEquals(CMakeBuildSpec.Init.create(Map.of(), "DEBUG"), compile.getDefines().iterator().next());
  }

  @Test
  void testVisibilityAsString() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.options(Map.of("visibility", "private"), "-O2");
    assertEquals(CMakeVisibility.PRIVATE, compile.getOptions().iterator().next().getVisibility());
  }

  @Test
  void testVisibilityAsConstant() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    compile.defines(Map.of("visibility", CMakeCompiling.PRIVATE), "INTERNAL");
    assertEquals(CMakeVisibility.PRIVATE, compile.getDefines().iterator().next().getVisibility());
  }

  @Test
  void testInvalidVisibilityName() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    assertThrows(CMakeApiException.class, () -> compile.options(Map.of("visibility", "protected"), "-O2"));
  }

  @Test
  void testInvalidVisibilityType() {
    CMakeLibraryCompiling compile = new CMakeLibraryCompiling();
    assertThrows(CMakeApiException.class, () -> compile.options(Map.of("visibility", 1), "-O2"));
  }
}
