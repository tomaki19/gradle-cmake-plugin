/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.extension;

import java.util.Set;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

public final class CMakeValidator {

  public void validateToolchains(final Set<CMakeToolchain> toolchains) {
    toolchains.forEach((toolchain) -> {
      validatePresent(toolchain.getOperatingSystem(),
          "toolchains -> %s -> operatingSystem".formatted(toolchain.getName()));
      validatePresent(toolchain.getCompiler(), "toolchains -> %s -> compiler".formatted(toolchain.getName()));
      validateNotEmpty(toolchain.getCompiler(), "toolchains -> %s -> compiler".formatted(toolchain.getName()));
      validatePresent(toolchain.getArchitecture(), "toolchains -> %s -> architecture".formatted(toolchain.getName()));
      validateNotEmpty(toolchain.getArchitecture(), "toolchains -> %s -> architecture".formatted(toolchain.getName()));
      validatePresent(toolchain.getGenerator(), "toolchains -> %s -> generator".formatted(toolchain.getName()));
      validateNotEmpty(toolchain.getGenerator(), "toolchains -> %s -> generator".formatted(toolchain.getName()));
    });
  }

  public void validateLibraries(final Set<CMakeLibrary> libraries) {
    libraries.forEach((library) -> {
      if (!library.getToolchains().get().isEmpty()) {
        validateNotEmpty(library.getToolchains(), "libraries -> %s -> toolchains".formatted(library.getName()));
      }
      validateNotEmpty(library.getIncludes(), "library -> %s -> includes".formatted(library.getName()));
    });
  }

  public void validateApplications(final Set<CMakeApplication> applications) {
    applications.forEach((application) -> {
      validateNotEmpty(application.getToolchains(), "application -> %s -> toolchains".formatted(application.getName()));
      validateNotEmpty(application.getSources(), "application -> %s -> sources".formatted(application.getName()));
    });
  }

  public void validateTests(final Set<CMakeTest> tests) {
    tests.forEach((test) -> {
      validateNotEmpty(test.getToolchains(), "test -> %s -> toolchains".formatted(test.getName()));
      validateNotEmpty(test.getSources(), "test -> %s -> sources".formatted(test.getName()));
    });
  }

  private static void validatePresent(final Property<?> property, final String message) {
    if (!property.isPresent()) {
      throw new IllegalArgumentException("Required option is missing: %s!".formatted(message));
    }
  }

  private static void validateNotEmpty(final Property<String> property, final String name) {
    if (property.get().isBlank()) {
      throw new IllegalArgumentException("Required option is empty: %s!".formatted(name));
    }
  }

  private static void validateNotEmpty(final SetProperty<String> property, final String name) {
    if (property.get().isEmpty()) {
      throw new IllegalArgumentException("Required option is empty: %s!".formatted(name));
    }
  }

}
