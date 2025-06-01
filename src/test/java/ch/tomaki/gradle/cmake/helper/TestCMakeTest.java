/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.helper;

import java.util.Arrays;

import org.gradle.api.NamedDomainObjectProvider;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;

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
      final String toolchainName) {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().set(Arrays.asList(toolchainName));
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension,
      final String toolchainName, final String... dependencies) {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension, toolchainName);
    provider.configure((object) -> {
      object.getPrivateLinkDependencies().set(Arrays.asList(dependencies));
    });
    return provider;
  }

  private TestCMakeTest() {
  }

}
