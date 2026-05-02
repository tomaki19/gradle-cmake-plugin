/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.NamedDomainObjectProvider;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class TestCMakeToolchain {

  public static NamedDomainObjectProvider<CMakeToolchain> register(final String name, final CMakeExtension extension) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = extension.getToolchains().register(name);
    provider.configure((object) -> {
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithLibraryDependencies(final String name,
      final CMakeExtension extension, final Collection<Map<String, Object>> options,
      final Collection<Map<String, Object>> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      options.forEach(opt -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> names = (Collection<CharSequence>) opt.get("names");
        Map<String, Object> spec = new HashMap<>(opt);
        spec.remove("names");
        object.getLibraries().getLinking().options(spec, names.toArray(new CharSequence[0]));
      });
      dependencies.forEach(dep -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> components = (Collection<CharSequence>) dep.get("components");
        Map<String, Object> spec = new HashMap<>(dep);
        spec.remove("components");
        object.getLibraries().getLinking().link(spec, components.toArray(new CharSequence[0]));
      });
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithApplicationDependencies(final String name,
      final CMakeExtension extension, final Collection<Map<String, Object>> options,
      final Collection<Map<String, Object>> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      options.forEach(opt -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> names = (Collection<CharSequence>) opt.get("names");
        Map<String, Object> spec = new HashMap<>(opt);
        spec.remove("names");
        object.getApplications().getLinking().options(spec, names.toArray(new CharSequence[0]));
      });
      dependencies.forEach(dep -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> components = (Collection<CharSequence>) dep.get("components");
        Map<String, Object> spec = new HashMap<>(dep);
        spec.remove("components");
        object.getApplications().getLinking().link(spec, components.toArray(new CharSequence[0]));
      });
    });
    return provider;
  }

  public static NamedDomainObjectProvider<CMakeToolchain> registerWithTestDependencies(final String name,
      final CMakeExtension extension, final Collection<Map<String, Object>> options,
      final Collection<Map<String, Object>> dependencies) {
    final NamedDomainObjectProvider<CMakeToolchain> provider = register(name, extension);
    provider.configure((object) -> {
      options.forEach(opt -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> names = (Collection<CharSequence>) opt.get("names");
        Map<String, Object> spec = new HashMap<>(opt);
        spec.remove("names");
        object.getTests().getLinking().options(spec, names.toArray(new CharSequence[0]));
      });
      dependencies.forEach(dep -> {
        @SuppressWarnings("unchecked")
        Collection<CharSequence> components = (Collection<CharSequence>) dep.get("components");
        Map<String, Object> spec = new HashMap<>(dep);
        spec.remove("components");
        object.getTests().getLinking().link(spec, components.toArray(new CharSequence[0]));
      });
    });
    return provider;
  }

  private TestCMakeToolchain() {
  }

}
