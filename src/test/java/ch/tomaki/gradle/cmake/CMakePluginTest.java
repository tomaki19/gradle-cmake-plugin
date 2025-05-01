/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;

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

  @Test
  void toolchains() {
    final Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(CMakePlugin.class);
    final CMakeExtension extension = project.getExtensions().getByType(CMakeExtension.class);

    final String toolchainName = "TestToolchain";
    final OperatingSystem operatingSystem = OperatingSystem.WINDOWS;
    final String architecture = "aarch64";
    final String compiler = "mscv";
    final String generator = "Visual Studio 2022";

    final NamedDomainObjectProvider<CMakeToolchain> toolchainProvider = extension
        .getToolchains()
        .register(
            toolchainName,
            (toolchain) -> {
              toolchain.getArchitecture().set(architecture);
              toolchain.getOperatingSystem().set(operatingSystem);
              toolchain.getCompiler().set(compiler);
              toolchain.getGenerator().set(generator);
            });
    assertTrue(toolchainProvider.isPresent());
    assertEquals(toolchainName, toolchainProvider.getName());
    assertEquals(architecture, toolchainProvider.get().getArchitecture().get());
    assertEquals(operatingSystem, toolchainProvider.get().getOperatingSystem().get());
    assertEquals(compiler, toolchainProvider.get().getCompiler().get());
    assertEquals(generator, toolchainProvider.get().getGenerator().get());
  }
}
