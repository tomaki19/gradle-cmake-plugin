/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Arrays;
import java.util.Set;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class TestCMakeTest {

  public static NamedDomainObjectProvider<CMakeTest> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeTest> provider = extension.getTests().register(name);
    provider.configure((object) -> {
      object.getHeaders().set(Arrays.asList("header0"));
      object.getSources().set(Arrays.asList("source0"));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> register(final String name, final CMakeExtension extension,
      final Set<String> toolchains) {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().set(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Set<String> toolchains, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  private TestCMakeTest() {
  }

}
