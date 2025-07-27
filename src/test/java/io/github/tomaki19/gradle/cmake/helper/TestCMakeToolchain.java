/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Arrays;
import java.util.Set;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class TestCMakeToolchain {

  public static NamedDomainObjectProvider<CMakeToolchain> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains().register(name);
    provider.configure((object) -> {
      object.getOperatingSystem().set(OperatingSystem.current());
      object.getBuildConfigs().set(Arrays.asList("release", "debug"));

    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithBinaryDependencies(final String name,
      final CMakeExtension extension, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getBinaries().getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithLibraryDependencies(final String name,
      final CMakeExtension extension, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getLibraries().getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithApplicationDependencies(final String name,
      final CMakeExtension extension, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getApplications().getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithTestDependencies(final String name,
      final CMakeExtension extension, final Set<String> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getTests().getPrivateLinkDependencies().set(dependencies);
    });
    return provider;
  }

  private TestCMakeToolchain() {
  }

}
