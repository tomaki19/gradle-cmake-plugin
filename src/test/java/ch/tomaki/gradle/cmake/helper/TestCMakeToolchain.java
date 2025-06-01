/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import java.util.Arrays;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

public final class TestCMakeToolchain {

  public static NamedDomainObjectProvider<CMakeToolchain> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains().register(name);
    provider.configure((object) -> {
      object.getOperatingSystem().set(OperatingSystem.current());
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithLibraryDependencies(final String name,
      final CMakeExtension extension, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getLibraries().getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithApplicationDependencies(final String name,
      final CMakeExtension extension, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getApplications().getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithTestDependencies(final String name,
      final CMakeExtension extension, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      object.getTests().getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  private TestCMakeToolchain() {
  }

}
