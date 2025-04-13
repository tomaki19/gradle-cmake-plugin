/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.NamedDomainObjectContainer;

import ch.tomaki.gradle.cmake.extension.CMakeBinary;

public interface CMakeResolver<T extends CMakeBinary> {

  void accept(final T cmakeBinary, final CMakeResolvedToolchain resolvedToolchain, final String buildConfig);

  static <T extends CMakeBinary> void forBinaries(final NamedDomainObjectContainer<T> cmakeBinaries,
      final CMakeResolvedBuild resolvedBuild, final CMakeResolver<T> resolver) {
    cmakeBinaries.forEach((cmakeBinary) -> {
      cmakeBinary.getBuildToolchains().get().forEach((toolchainName) -> {
        resolvedBuild.forToolchain(toolchainName, (toolchain) -> {
          toolchain.getBuildConfigs().forEach((buildConfig) -> {
            resolver.accept(cmakeBinary, toolchain, buildConfig);
          });
        });
      });
    });
  }

}
