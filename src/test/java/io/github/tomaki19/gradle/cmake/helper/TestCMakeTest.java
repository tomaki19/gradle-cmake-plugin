/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.net.URISyntaxException;
import java.util.Collection;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;

public final class TestCMakeTest {

  public static NamedDomainObjectProvider<CMakeTest> register(final String name, final CMakeExtension extension)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeTest> provider = extension.getTests().register(name);
    final String headerPath = TestCMakeApplication.class.getResource("src/cpp").toURI().getPath();
    final String sourcePath = TestCMakeApplication.class.getResource("src/hpp").toURI().getPath();
    provider.configure((object) -> {
      object.getHeaders().srcDir(headerPath);
      object.getSources().srcDir(sourcePath);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> register(final String name, final CMakeExtension extension,
      final Collection<String> toolchains) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension);
    provider.configure((object) -> {
      object.getToolchains().addAll(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateLinking().getDependencies().addAll(dependencies);
      object.getPrivateLinking().getOptions().addAll(options);
    });
    return provider;
  }

  private TestCMakeTest() {
  }

}
