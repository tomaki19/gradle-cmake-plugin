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
      final Collection<String> toolchains) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.toolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateInterfaceDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateInterfaceLinking().dependencies(dependencies);
      object.getPrivateInterfaceLinking().options(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateStaticDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateStaticLinking().dependencies(dependencies);
      object.getPrivateStaticLinking().options(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateSharedDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPrivateSharedLinking().dependencies(dependencies);
      object.getPrivateSharedLinking().options(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicInterfaceDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPublicInterfaceLinking().dependencies(dependencies);
      object.getPublicInterfaceLinking().options(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicStaticDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPublicStaticLinking().dependencies(dependencies);
      object.getPublicStaticLinking().options(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicSharedDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<CMakeDependencies> dependencies, Collection<String> options) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      object.getPublicSharedLinking().dependencies(dependencies);
      object.getPublicSharedLinking().options(options);
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
