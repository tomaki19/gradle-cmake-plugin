/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Arrays;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class TestCMakeInterfaceLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    provider.configure((object) -> {
      object.getHeaders().set(Arrays.asList("header0"));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicDependencies(final String name,
      final CMakeExtension extension, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPublicLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  private TestCMakeInterfaceLibrary() {
  }

}
