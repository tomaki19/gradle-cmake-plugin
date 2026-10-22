/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinkSpec;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

/** Link dependencies with {@code forBuild} only apply to the given build variants of a library. */
class CMakeLibraryForBuildResolverTest {

  private static final List<Map<String, Object>> DEPENDENCIES = List.of(
      Map.of("components", List.of("always"), CMakeLibraryLinkSpec.PROJECT, "Pkg"),
      Map.of("components", List.of("staticOnly"), CMakeLibraryLinkSpec.PROJECT, "Pkg",
          CMakeLibraryLinkSpec.BUILD_VARIANT, CMakeBuildVariant.STATIC),
      Map.of("components", List.of("sharedOnly"), CMakeLibraryLinkSpec.PROJECT, "Pkg",
          CMakeLibraryLinkSpec.BUILD_VARIANT, "shared"),
      Map.of("components", List.of("both"), CMakeLibraryLinkSpec.PROJECT, "Pkg",
          CMakeLibraryLinkSpec.BUILD_VARIANT, List.of(CMakeLinkVariant.SHARED, CMakeLinkVariant.STATIC)));

  private Project project;
  private CMakeExtension extension;

  @BeforeEach
  void setUp() {
    project = ProjectBuilder.builder().build();
    extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class, project.getTasks());
    TestCMakePackage.register("Pkg", extension);
    TestCMakeToolchain.register("Toolchain0", extension);
  }

  @Test
  void appliesDependenciesPerBuildVariant() throws Exception {
    TestCMakeBinaryLibrary.registerWithDependencies("Lib", extension, List.of("Toolchain0"),
        CMakeBuildVariant.SHARED, List.of(), DEPENDENCIES)
        .configure((library) -> library.buildVariants(CMakeBuildVariant.SHARED, CMakeBuildVariant.STATIC));

    final CMakeResolvedToolchain toolchain = resolve();

    assertEquals(Set.of("always", "sharedOnly", "both"),
        packageDependencyNames(toolchain.getSharedLibraries()));
    assertEquals(Set.of("always", "staticOnly", "both"),
        packageDependencyNames(toolchain.getStaticLibraries()));
  }

  @Test
  void interfaceLibraryOnlyGetsUnrestrictedDependencies() throws Exception {
    TestCMakeInterfaceLibrary.register("Headers", extension)
        .configure((library) -> DEPENDENCIES.forEach((dependency) -> {
          final Map<String, Object> spec = new java.util.HashMap<>(dependency);
          @SuppressWarnings("unchecked")
          final Collection<CharSequence> components = (Collection<CharSequence>) spec.remove("components");
          library.getLinking().link(spec, components.toArray(new CharSequence[0]));
        }));

    final CMakeResolvedToolchain toolchain = resolve();

    assertEquals(Set.of("always"), packageDependencyNames(toolchain.getInterfaceLibraries()));
  }

  private CMakeResolvedToolchain resolve() {
    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> toolchains = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());
    assertEquals(1, toolchains.size());
    return toolchains.iterator().next();
  }

  private static Set<String> packageDependencyNames(final Collection<CMakeResolvedLibrary> libraries) {
    assertEquals(1, libraries.size());
    return libraries.iterator().next().getAllPackageDependencies().stream()
        .map(CMakeResolvedPackageDependency::getName)
        .collect(Collectors.toSet());
  }

}
