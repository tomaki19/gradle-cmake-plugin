/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import org.gradle.api.NamedDomainObjectContainer;

import ch.tomaki.gradle.cmake.extension.CMakeObject;

public final class CMakeResolver<T extends CMakeObject, R extends CMakeResolvedBinary> {

  public static <O extends CMakeObject, R extends CMakeResolvedObject> void process(
      final NamedDomainObjectContainer<O> cmakeObjects, final CMakeResolvedBuild resolvedBuild,
      final ResolverWithToolchain<O, R> resolverWithToolchain, final AcceptorWithToolchain<R> acceptorWithToolchain) {
    cmakeObjects.forEach((cmakeObject) -> {
      if (cmakeObject.getBuildToolchains().get().isEmpty()) {
        resolvedBuild.add(new CMakeResolvedInterface(cmakeObject));
      } else {
        cmakeObject.getBuildToolchains().get().forEach((toolchainName) -> {
          resolvedBuild.forToolchain(toolchainName, (toolchain) -> {
            toolchain.getBuildConfigs().forEach((buildConfig) -> {
              final R resolvedBinary = resolverWithToolchain.resolve(cmakeObject, toolchain, buildConfig);
              acceptorWithToolchain.accept(resolvedBinary, toolchain, buildConfig);
            });
          });
        });
      }
    });
  }

  public interface ResolverWithToolchain<O extends CMakeObject, R extends CMakeResolvedObject> {
    R resolve(final O cmakeObject, final CMakeResolvedToolchain resolvedToolchain, final String buildConfig);
  }

  public interface AcceptorWithToolchain<R extends CMakeResolvedObject> {
    void accept(final R cmakeResolvedObject, final CMakeResolvedToolchain resolvedToolchain, final String buildConfig);
  }

}
