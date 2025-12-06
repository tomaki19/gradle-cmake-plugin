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

public final class TestCMakeInterfaceLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    final String headerPath = TestCMakeInterfaceLibrary.class.getResource("src/cpp").toURI().getPath();
    provider.configure((object) -> {
      object.getHeaders().srcDir(headerPath);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateInterfaceDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPrivateInterfaceLinking().getDependencies().addAll(dependencies);
      object.getPrivateInterfaceLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateStaticDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPrivateStaticLinking().getDependencies().addAll(dependencies);
      object.getPrivateStaticLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPrivateSharedDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPrivateSharedLinking().getDependencies().addAll(dependencies);
      object.getPrivateSharedLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicInterfaceDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPublicInterfaceLinking().getDependencies().addAll(dependencies);
      object.getPublicInterfaceLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicStaticDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPublicStaticLinking().getDependencies().addAll(dependencies);
      object.getPublicStaticLinking().getOptions().addAll(options);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithPublicSharedDependencies(final String name,
      final CMakeExtension extension, final Collection<CMakeDependencies> dependencies, Collection<String> options)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension);
    provider.configure((object) -> {
      object.getPublicSharedLinking().getDependencies().addAll(dependencies);
      object.getPublicSharedLinking().getOptions().addAll(options);
    });
    return provider;
  }

  private TestCMakeInterfaceLibrary() throws URISyntaxException {
  }

}
