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
      final CMakeExtension extension, final Collection<String> toolchains,
      final Collection<Map<String, Object>> options,
      final Collection<Map<String, Object>> dependencies)
      throws URISyntaxException {
    final NamedDomainObjectProvider<CMakeTest> provider = register(name, extension, toolchains);
    provider.configure((object) -> {
      options.forEach(opt -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> names = (Collection<CharSequence>) opt.get("names");
        Map<String, Object> spec = new HashMap<>(opt);
        spec.remove("names");
        object.getLinking().options(names, spec);
      });
      dependencies.forEach(dep -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> components = (Collection<CharSequence>) dep.get("components");
        Map<String, Object> spec = new HashMap<>(dep);
        spec.remove("components");
        object.getLinking().link(components, spec);
      });
    });
    return provider;
  }

  private TestCMakeTest() {
  }

}
