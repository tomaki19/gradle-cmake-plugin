/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Collection;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class TestCMakeBinaryLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    provider.configure((object) -> {
      object.getHeaders().add("header0");
      object.getSources().add("source0");
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension,
      final Collection<CharSequence> toolchains) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.setToolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Collection<CharSequence> toolchains,
      final Collection<CharSequence> dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.setPrivateLinkDependencies(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicDependencies(final String name,
      final CMakeExtension extension, final Collection<CharSequence> toolchains,
      final Collection<CharSequence> dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.setPublicLinkDependencies(dependencies);
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
