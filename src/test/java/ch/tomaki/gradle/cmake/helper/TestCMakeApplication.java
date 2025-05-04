/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import java.util.Arrays;

import org.gradle.api.NamedDomainObjectProvider;

import ch.tomaki.gradle.cmake.extension.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.CMakeExtension;

public final class TestCMakeApplication {

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name,
      final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeApplication> provider = extension.getApplications().register(name);
    provider.configure((object) -> {
      object.getIncludes().set(Arrays.asList("inlude0"));
      object.getSources().set(Arrays.asList("source0"));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name, final CMakeExtension extension,
      final String toolchainName) {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().set(Arrays.asList(toolchainName));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension,
      final String toolchainName, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension, toolchainName);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  private TestCMakeApplication() {
  }

}
