/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import java.util.Arrays;
import java.util.Set;

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
      final Set<String> toolchains) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().set(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Set<String> toolchains, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicDependencies(final String name,
      final CMakeExtension extension, final Set<String> toolchains, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPublicLinkDependencies().set(dependencies);
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
