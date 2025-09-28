/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.CMakeValidator;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public class CMakeValidatorTest {

  @Test
  void validateToolchains() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
        project.getTasks());

    final String name = "Toolchain";
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains().register(name);
    // assertThrows(IllegalArgumentException.class, () ->
    // CMakeValidator.validateToolchains(extension.getToolchains()));

    final OperatingSystem operatingSystem = OperatingSystem.WINDOWS;
    provider.configure((toolchain) -> {
      toolchain.setOperatingSystem(operatingSystem);
    });
    // assertThrows(IllegalArgumentException.class, () ->
    // CMakeValidator.validateToolchains(extension.getToolchains()));

    final String generator = "Visual Studio 2022";
    provider.configure((toolchain) -> {
      toolchain.setGenerator(generator);
    });
    assertEquals(name, provider.get().getName());
    assertEquals(operatingSystem, provider.get().getOperatingSystem().get());
    assertEquals(generator, provider.get().getGenerator().get());
  }

  @Test
  void validateHeaderOnlyLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
        project.getTasks());

    final String name = "Library";
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Collection<CharSequence> headers = new HashSet<>(Arrays.asList("i0", "i1"));
    provider.configure((library) -> {
      library.setHeaders(headers);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(headers, provider.get().getHeaders());
  }

  @Test
  void validateBinaryLibraries() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
        project.getTasks());

    final String name = "Library";
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries()
        .register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Collection<CharSequence> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((library) -> {
      library.setToolchains(toolchains);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateLibraries(extension.getLibraries()));

    final Collection<CharSequence> headers = new HashSet<>(Arrays.asList("i0", "i1"));
    provider.configure((library) -> {
      library.setHeaders(headers);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains());
    assertIterableEquals(headers, provider.get().getHeaders());
  }

  @Test
  void validateApplications() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
        project.getTasks());

    final String name = "Application";
    final NamedDomainObjectProvider<CMakeApplication> provider = extension.getApplications()
        .register(name);
    assertThrows(IllegalArgumentException.class,
        () -> CMakeValidator.validateApplications(extension.getApplications()));

    final Collection<CharSequence> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((application) -> {
      application.setToolchains(toolchains);
    });
    assertThrows(IllegalArgumentException.class,
        () -> CMakeValidator.validateApplications(extension.getApplications()));

    final Collection<CharSequence> sources = new HashSet<>(Arrays.asList("s0", "s1"));
    provider.configure((application) -> {
      application.setSources(sources);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains());
    assertIterableEquals(sources, provider.get().getSources());
  }

  @Test
  void validateTests() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class,
        project.getTasks());

    final String name = "Test";
    final NamedDomainObjectProvider<CMakeTest> provider = extension.getTests()
        .register(name);
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateTests(extension.getTests()));

    final Collection<CharSequence> toolchains = new HashSet<>(Arrays.asList("t0", "t1"));
    provider.configure((test) -> {
      test.setToolchains(toolchains);
    });
    assertThrows(IllegalArgumentException.class, () -> CMakeValidator.validateTests(extension.getTests()));

    final Collection<CharSequence> sources = new HashSet<>(Arrays.asList("s0", "s1"));
    provider.configure((test) -> {
      test.setSources(sources);
    });
    assertEquals(name, provider.get().getName());
    assertIterableEquals(toolchains, provider.get().getToolchains());
    assertIterableEquals(sources, provider.get().getSources());
  }

}
