/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.extension;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;

import io.github.tomaki19.gradle.cmake.model.CMakeConfigurationConventions;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedBinary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeResolvedToolchain;

public class CMakeConfigurationContainer {

  private final ConfigurationContainer configurations;

  public CMakeConfigurationContainer(final ConfigurationContainer configurations) {
    this.configurations = configurations;
  }

  public Configuration createModulesConfiguration(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createModulesName(executable, toolchain,
        buildConfig));
  }

  public Configuration createModulesConfiguration(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createModulesName(library, toolchain, buildConfig));
  }

  public Configuration createRuntimeConfiguration(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createRuntimeName(executable, toolchain, buildConfig));
  }

  public Configuration createRuntimeConfiguration(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createRuntimeName(library, toolchain, buildConfig));
  }

  public Configuration createDevelopConfiguration(final CMakeResolvedBinary<?> executable,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createDevelopName(executable, toolchain, buildConfig));
  }

  public Configuration createDevelopConfiguration(final CMakeResolvedLibrary library,
      final CMakeResolvedToolchain toolchain, final String buildConfig) {
    return createInConfiguration(CMakeConfigurationConventions.createDevelopName(library, toolchain, buildConfig));
  }

  private Configuration createInConfiguration(final String target) {
    return configurations.create(target, (newConfiguration) -> {
      newConfiguration.setCanBeDeclared(true);
      newConfiguration.setCanBeResolved(true);
      newConfiguration.setCanBeConsumed(true);
    });
  }

}
