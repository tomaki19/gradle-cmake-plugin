/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import java.util.Arrays;

import org.gradle.api.NamedDomainObjectProvider;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;

public final class TestCMakeBinaryLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    provider.configure((object) -> {
      object.getHeaders().set(Arrays.asList("header0"));
      object.getSources().set(Arrays.asList("source0"));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension,
      final String toolchainName) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().set(Arrays.asList(toolchainName));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension,
      final String toolchainName, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchainName);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicDependencies(final String name,
      final CMakeExtension extension,
      final String toolchainName, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchainName);
    provider.configure((object) -> {
      object.getPublicLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
