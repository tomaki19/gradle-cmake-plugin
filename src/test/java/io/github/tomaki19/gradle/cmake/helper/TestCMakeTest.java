/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.net.URISyntaxException;
import java.util.Collection;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildItems;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableDependencies;
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
      object.toolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeTest> registerWithDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains, final Collection<CMakeBuildItems> options,
      final Collection<CMakeExecutableDependencies> dependencies)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getLinking().options(options);
      object.getLinking().dependencies(dependencies);
    });
    return provider;
  }

  private TestCMakeTest() {
  }

}
