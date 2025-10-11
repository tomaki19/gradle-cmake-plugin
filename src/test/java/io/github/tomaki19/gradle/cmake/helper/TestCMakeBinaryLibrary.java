/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.net.URISyntaxException;
import java.util.Collection;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;

public final class TestCMakeBinaryLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    final String headerPath = TestCMakeBinaryLibrary.class.getResource("src/cpp").toURI().getPath();
    final String sourcePath = TestCMakeBinaryLibrary.class.getResource("src/hpp").toURI().getPath();
    provider.configure((object) -> {
      object.getHeaders().srcDir(headerPath);
      object.getSources().srcDir(sourcePath);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension,
      final Collection<CharSequence> toolchains) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.setToolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateDependencies(final String name,
      final CMakeExtension extension, final Collection<CharSequence> toolchains,
      final Collection<CharSequence> dependencies) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.setPrivateLinkDependencies(dependencies);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicDependencies(final String name,
      final CMakeExtension extension, final Collection<CharSequence> toolchains,
      final Collection<CharSequence> dependencies) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.setPublicLinkDependencies(dependencies);
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
