/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeValidator {

  public static void validateToolchains(final Set<CMakeToolchain> toolchains) {
    toolchains.forEach((toolchain) -> {


      // validatePresent(toolchain.getOperatingSystem(),
      // "toolchains -> %s -> operatingSystem".formatted(toolchain.getName()));
      // validatePresent(toolchain.getGenerator(), "toolchains -> %s ->
      // generator".formatted(toolchain.getName()));
      // validateNotEmpty(toolchain.getGenerator(), "toolchains -> %s ->
      // generator".formatted(toolchain.getName()));
      // if (toolchain.getBuildConfigs().isEmpty()) {
      // toolchain.getBuildConfigs().set(Set.of("debug", "release"));
      // }
    });
  }

  public static void validateLibraries(final Set<CMakeLibrary> libraries) {
    libraries.forEach((library) -> {
      validateNotEmpty(library.getHeaders(), "library -> %s -> headers".formatted(library.getName()));
    });
  }

  public static void validateApplications(final Set<CMakeApplication> applications) {
    applications.forEach((application) -> {
      validateNotEmpty(application.getToolchains(), "application -> %s -> toolchains".formatted(application.getName()));
      validateNotEmpty(application.getSources(), "application -> %s -> sources".formatted(application.getName()));
    });
  }

  public static void validateTests(final Set<CMakeTest> tests) {
    tests.forEach((test) -> {
      validateNotEmpty(test.getToolchains(), "test -> %s -> toolchains".formatted(test.getName()));
      validateNotEmpty(test.getSources(), "test -> %s -> sources".formatted(test.getName()));
    });
  }

  private static void validate(final Optional<?> property, final String message) {
    if (property.isEmpty()) {
      throw new IllegalArgumentException("Required option is missing: %s!".formatted(message));
    }
  }

  private static void validateNotEmpty(final Collection<?> property, final String name) {
    if (property.isEmpty()) {
      throw new IllegalArgumentException("Required option is missing: %s!".formatted(name));
    }
  }

  private CMakeValidator() {
  }
}
