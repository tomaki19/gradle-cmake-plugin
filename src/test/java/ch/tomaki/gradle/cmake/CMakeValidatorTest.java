/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.CMakeValidator;
import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public class CMakeValidatorTest {

  @Test
  void validateToolchains() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String name = "Toolchain";
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains()
        .register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateToolchains(extension.getToolchains()));

    final OperatingSystem operatingSystem = OperatingSystem.WINDOWS;
    provider.configure((toolchain) -> {
      toolchain.getOperatingSystem().set(operatingSystem);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateToolchains(extension.getToolchains()));

    final String architecture = "aarch64";
    provider.configure((toolchain) -> {
      toolchain.getArchitecture().set(architecture);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateToolchains(extension.getToolchains()));

    final String compiler = "mscv";
    provider.configure((toolchain) -> {
      toolchain.getCompiler().set(compiler);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateToolchains(extension.getToolchains()));

    final String generator = "Visual Studio 2022";
    provider.configure((toolchain) -> {
      toolchain.getGenerator().set(generator);
    });
    assertEquals(name, provider.get().getName());
    assertEquals(operatingSystem, provider.get().getOperatingSystem().get());
    assertEquals(compiler, provider.get().getCompiler().get());
    assertEquals(generator, provider.get().getGenerator().get());
  }

  @Test
  void validateHeaderOnlyLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String name = "Library";
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Set<String> includes = new HashSet<>(Arrays.asList("i0", "i1"));
    provider.configure((library) -> {
      library.getHeaders().set(includes);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(includes, provider.get().getHeaders().get());
  }

  @Test
  void validateBinaryLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String name = "Library";
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries()
        .register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Set<String> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((library) -> {
      library.getToolchains().set(toolchains);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Set<String> includes = new HashSet<>(Arrays.asList("i0", "i1"));
    provider.configure((library) -> {
      library.getHeaders().set(includes);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains().get());
    assertIterableEquals(includes, provider.get().getHeaders().get());
  }

  @Test
  void validateApplications() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String name = "Application";
    final NamedDomainObjectProvider<CMakeApplication> provider = extension.getApplications()
        .register(name);
    assertThrows(IllegalArgumentException.class,
        () -> CMakeValidator.validateApplications(extension.getApplications()));

    final Set<String> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((application) -> {
      application.getToolchains().set(toolchains);
    });
    assertThrows(IllegalArgumentException.class,
        () -> CMakeValidator.validateApplications(extension.getApplications()));

    final Set<String> sources = new HashSet<>(Arrays.asList("s0", "s1"));
    provider.configure((application) -> {
      application.getSources().set(sources);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains().get());
    assertIterableEquals(sources, provider.get().getSources().get());
  }

  @Test
  void validateTests() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    final String name = "Test";
    final NamedDomainObjectProvider<CMakeTest> provider = extension.getTests()
        .register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateTests(extension.getTests()));

    final Set<String> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((test) -> {
      test.getToolchains().set(toolchains);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateTests(extension.getTests()));

    final Set<String> sources = new HashSet<>(Arrays.asList("s0", "s1"));
    provider.configure((test) -> {
      test.getSources().set(sources);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains().get());
    assertIterableEquals(sources, provider.get().getSources().get());
  }

}
