/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Collection;
import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class TestCMakeToolchain {

  public static NamedDomainObjectProvider<CMakeToolchain> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains().register(name);
    provider.configure((object) -> {
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithBinaryDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getBinaries().getPrivateLinking().getDependencies().addAll(dependencies);
      object.getBinaries().getPrivateLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithLibraryDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getLibraries().getPrivateLinking().getDependencies().addAll(dependencies);
      object.getLibraries().getPrivateLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithApplicationDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getApplications().getPrivateLinking().getDependencies().addAll(dependencies);
      object.getApplications().getPrivateLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithTestDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getTests().getPrivateLinking().getDependencies().addAll(dependencies);
      object.getTests().getPrivateLinking().getOptions().addAll(options);
    });
    return provider;
  }

  private TestCMakeToolchain() {
  }

}
