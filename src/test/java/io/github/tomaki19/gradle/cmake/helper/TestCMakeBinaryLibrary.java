/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.model.CMakeBuildVariant;

public final class TestCMakeBinaryLibrary {

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension,
      final CMakeBuildVariant buildVariant) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = extension.getLibraries().register(name);
    final String headerPath = TestCMakeBinaryLibrary.class.getResource("src/cpp").toURI().getPath();
    final String sourcePath = TestCMakeBinaryLibrary.class.getResource("src/hpp").toURI().getPath();
    provider.configure((object) -> {
      object.getHeaders().srcDir(headerPath);
      object.getSources().srcDir(sourcePath);
      object.buildVariants(buildVariant);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> register(final String name, final CMakeExtension extension,
      final Collection<String> toolchains, final CMakeBuildVariant buildVariant) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, buildVariant);
    provider.configure((object) -> {
      object.toolchains(toolchains);
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeLibrary> registerWithDependencies(final String name,
      final CMakeExtension extension, final Collection<String> toolchains, final CMakeBuildVariant buildVariant,
      final Collection<Map<String, Object>> options,
      final Collection<Map<String, Object>> dependencies) throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeLibrary> provider = register(name, extension, toolchains, buildVariant);
    provider.configure((object) -> {
      options.forEach(opt -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> names = (Collection<CharSequence>) opt.get("names");
        Map<String, Object> spec = new HashMap<>(opt);
        spec.remove("names");
        object.getLinking().options(spec, names.toArray(new CharSequence[0]));
      });
      dependencies.forEach(dep -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> components = (Collection<CharSequence>) dep.get("components");
        Map<String, Object> spec = new HashMap<>(dep);
        spec.remove("components");
        object.getLinking().link(spec, components.toArray(new CharSequence[0]));
      });
    });
    return provider;
  }

  private TestCMakeBinaryLibrary() {
  }

}
