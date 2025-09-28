/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Set;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;

public final class TestCMakeApplication {

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name,
      final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeApplication> provider = extension.getApplications().register(name);
    provider.configure((object) -> {
      object.getHeaders().add("header0");
      object.getSources().add("source0");
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name, final CMakeExtension extension,
      final Set<String> toolchains) {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().addAll(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Set<String> toolchains, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().addAll(dependencies);
    });
    return provider;
  }

  private TestCMakeApplication() {
  }

}
