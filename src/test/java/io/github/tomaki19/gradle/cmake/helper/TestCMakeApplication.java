/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.net.URISyntaxException;
import java.util.Collection;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildItems;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableDependencies;

public final class TestCMakeApplication {

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name,
      final CMakeExtension extension) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeApplication> provider = extension.getApplications().register(name);
    final String headerPath = TestCMakeApplication.class.getResource("src/cpp").toURI().getPath();
    final String sourcePath = TestCMakeApplication.class.getResource("src/hpp").toURI().getPath();
    provider.configure((object) -> {
      object.getHeaders().srcDir(headerPath);
      object.getSources().srcDir(sourcePath);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> register(final String name, final CMakeExtension extension,
      final Collection<String> toolchains) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension);
    provider.configure((object) -> {
      object.toolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeApplication> registerWithDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains, final Collection<CMakeBuildItems> options,
      final Collection<CMakeExecutableDependencies> dependencies)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeApplication> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getLinking().options(options);
      object.getLinking().link(dependencies);
    });
    return provider;
  }

  private TestCMakeApplication() {
  }

}
