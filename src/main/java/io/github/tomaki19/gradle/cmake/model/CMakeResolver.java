/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinaryDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildItems;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableCompiling;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryCompiling;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolver {

  private final Project currentProject;
  private final Map<String, CMakePackage> availablePackages;
  private final Map<String, CMakeToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakePackage> packages, final Set<CMakeToolchain> toolchains) {
    Objects.requireNonNull(project, "Project must not be null!");
    Objects.requireNonNull(packages, "Packages must not be null!");
    Objects.requireNonNull(toolchains, "Toolchains must not be null!");
    this.currentProject = project; // SpotBugs: Project is a Gradle API object, not meant to be mutated by plugin
    this.availablePackages = packages.stream()
        .collect(Collectors.toUnmodifiableMap(CMakePackage::getName, Function.identity()));
    this.availableToolchains = toolchains.stream()
        .filter((toolchain) -> Objects.isNull(toolchain.getOperatingSystem())
            || Objects.equals(OperatingSystem.current(),
                toolchain.getOperatingSystem().getOrElse(OperatingSystem.current())))
        .collect(Collectors.toUnmodifiableMap(CMakeToolchain::getName, Function.identity()));

  }

  public Collection<CMakeResolvedToolchain> process(
      final Set<CMakeLibrary> libraries, final Set<CMakeApplication> applications, final Set<CMakeTest> tests) {
    final Map<String, CMakeResolvedToolchain> resolvedToolchains = new HashMap<>();
    resolveLibraries(resolvedToolchains, availableToolchains, libraries);
    resolveApplications(resolvedToolchains, availableToolchains, applications);
    resolveTests(resolvedToolchains, availableToolchains, tests);
    return resolvedToolchains.values().stream().sorted().toList();
  }

  private void resolveLibraries(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeLibrary> libraries) {
    libraries.forEach((component) -> processObject(component, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          validateLibrary(component);
          if (component.getSources().isEmpty()) {
            final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component, CMakeLinkType.INTERFACE,
                toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
            resolveLibrary(component, resolvedLibrary, toolchain, resolvedToolchain);
            resolvedToolchain.addInterfaceLibrary(resolvedLibrary);
          } else {
            if (toolchain.getLibraries().getBuildTypes().get().contains(CMakeBuildType.STATIC)
                || component.getBuildTypes().get().contains(CMakeBuildType.STATIC)) {
              final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component, CMakeLinkType.STATIC,
                  toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
              resolveLibrary(component, resolvedLibrary, toolchain, resolvedToolchain);
              resolvedToolchain.addStaticLibrary(resolvedLibrary);
            }
            if ((toolchain.getLibraries().getBuildTypes().get().isEmpty()
                || toolchain.getLibraries().getBuildTypes().get().contains(CMakeBuildType.SHARED))
                && (component.getBuildTypes().get().isEmpty()
                    || component.getBuildTypes().get().contains(CMakeBuildType.SHARED))) {
              final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component, CMakeLinkType.SHARED,
                  toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
              resolveLibrary(component, resolvedLibrary, toolchain, resolvedToolchain);
              resolvedToolchain.addSharedLibrary(resolvedLibrary);
            }
          }
        }));
  }

  private void resolveLibrary(final CMakeLibrary component, final CMakeResolvedLibrary resolvedLibrary,
      final CMakeToolchain toolchain, final CMakeResolvedToolchain resolvedToolchain) {
    resolveLibraryCompiling(Arrays.asList(toolchain.getLibraries().getCompiling(),
        component.getCompiling()), resolvedLibrary::addPrivateCompileDefinitions,
        resolvedLibrary::addPublicCompileDefinitions, resolvedLibrary::addPrivateCompileOptions,
        resolvedLibrary::addPublicCompileOptions);
    resolveLibraryLinking(Arrays.asList(toolchain.getLibraries().getLinking(), component.getLinking()),
        resolvedToolchain, resolvedLibrary.getLinkType(), resolvedLibrary::addPrivateLinkOption,
        resolvedLibrary::addPublicLinkOption, resolvedLibrary::addPrivatePackageDependency,
        resolvedLibrary::addPublicPackageDependency, resolvedLibrary::addPrivateProjectDependency,
        resolvedLibrary::addPublicProjectDependency);

  }

  private void resolveApplications(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeApplication> applications) {
    applications.forEach((component) -> processObject(component, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          validateExecutable(component);
          final CMakeResolvedExecutable resolvedApplication = new CMakeResolvedExecutable(component,
              toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE));
          resolveExecutableCompiling(Arrays.asList(toolchain.getApplications().getCompiling(),
              component.getCompiling()), resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPublicCompileDefinitions, resolvedApplication::addPrivateCompileOptions,
              resolvedApplication::addPublicCompileOptions);
          resolveExecutableLinking(Arrays.asList(toolchain.getApplications().getLinking(), component.getLinking()),
              resolvedToolchain, resolvedApplication::addPrivateLinkOption, resolvedApplication::addPublicLinkOption,
              resolvedApplication::addPrivatePackageDependency, resolvedApplication::addPublicPackageDependency,
              resolvedApplication::addPrivateProjectDependency, resolvedApplication::addPublicProjectDependency);
          resolvedToolchain.addApplication(resolvedApplication);
        }));
  }

  private void resolveTests(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeTest> tests) {
    tests.forEach((component) -> processObject(component, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          validateExecutable(component);
          final CMakeResolvedExecutable resolvedTest = new CMakeResolvedExecutable(component,
              toolchain.getTests().getStripDebug().getOrElse(Boolean.FALSE));
          resolveExecutableCompiling(Arrays.asList(toolchain.getTests().getCompiling(), component.getCompiling()),
              resolvedTest::addPrivateCompileDefinitions, resolvedTest::addPublicCompileDefinitions,
              resolvedTest::addPrivateCompileOptions, resolvedTest::addPublicCompileOptions);
          resolveExecutableLinking(Arrays.asList(toolchain.getTests().getLinking(), component.getLinking()),
              resolvedToolchain, resolvedTest::addPrivateLinkOption, resolvedTest::addPublicLinkOption,
              resolvedTest::addPrivatePackageDependency, resolvedTest::addPublicPackageDependency,
              resolvedTest::addPrivateProjectDependency, resolvedTest::addPublicProjectDependency);
          resolvedToolchain.addTest(resolvedTest);
        }));
  }

  private <U extends CMakeBinary> void processObject(final U binary, final Map<String, CMakeToolchain> toolchains,
      final Map<String, CMakeResolvedToolchain> resolvedToolchains, final Resolver resolver) {
    toolchains.forEach((toolchainName, toolchain) -> {
      if (binary.getToolchains().contains(toolchainName) || binary.getToolchains().isEmpty()) {
        if (!resolvedToolchains.containsKey(toolchainName)) {
          resolvedToolchains.put(toolchainName, new CMakeResolvedToolchain(toolchain));
        }
        resolver.resolve(toolchain, resolvedToolchains.get(toolchainName));
      }
    });
  }

  private interface Resolver {
    void resolve(final CMakeToolchain toolchain, final CMakeResolvedToolchain resolvedToolchain);
  }

  private void resolveExecutableCompiling(final Collection<CMakeExecutableCompiling> compileDefinitions,
      final Consumer<String> privateDefinitionConsumer, final Consumer<String> publicDefinitionConsumer,
      final Consumer<String> privateOptionConsumer, final Consumer<String> publicOptionConsumer) {
    for (final CMakeExecutableCompiling compileDefinition : compileDefinitions) {
      resolveCompiling(compileDefinition.getDefines(), compileDefinition.getOptions(), privateDefinitionConsumer,
          publicDefinitionConsumer, privateOptionConsumer, publicOptionConsumer);
    }
  }

  private void resolveLibraryCompiling(final Collection<CMakeLibraryCompiling> compileDefinitions,
      final Consumer<String> privateDefinitionConsumer, final Consumer<String> publicDefinitionConsumer,
      final Consumer<String> privateOptionConsumer, final Consumer<String> publicOptionConsumer) {
    for (final CMakeLibraryCompiling compileDefinition : compileDefinitions) {
      resolveCompiling(compileDefinition.getDefines(), compileDefinition.getOptions(), privateDefinitionConsumer,
          publicDefinitionConsumer, privateOptionConsumer, publicOptionConsumer);
    }
  }

  private void resolveCompiling(final Collection<CMakeBuildItems> defines, final Collection<CMakeBuildItems> options,
      final Consumer<String> privateDefinitionConsumer, final Consumer<String> publicDefinitionConsumer,
      final Consumer<String> privateOptionConsumer, final Consumer<String> publicOptionConsumer) {
    for (final CMakeBuildItems items : defines) {
      if (items.isPrivate()) {
        items.getNames().forEach(privateDefinitionConsumer);
      } else {
        items.getNames().forEach(publicDefinitionConsumer);
      }
    }
    for (final CMakeBuildItems items : options) {
      if (items.isPrivate()) {
        items.getNames().forEach(privateOptionConsumer);
      } else {
        items.getNames().forEach(publicOptionConsumer);
      }
    }
  }

  private void resolveExecutableLinking(final Collection<CMakeExecutableLinking> linkDefinitions,
      final CMakeResolvedToolchain toolchain,
      final Consumer<String> privateOptionConsumer, final Consumer<String> publicOptionConsumer,
      final Consumer<CMakeResolvedPackageDependency> privatePackageDependencyConsumer,
      final Consumer<CMakeResolvedPackageDependency> publicPackageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> privateProjectDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> publicProjectDependencyConsumer) {
    for (final CMakeExecutableLinking linkDefinition : linkDefinitions) {
      resolveLinkingOptions(linkDefinition.getOptions(), toolchain, privateOptionConsumer, publicOptionConsumer);
      for (final CMakeExecutableDependencies dependency : linkDefinition.getDependencies()) {
        resolveLinkingDependencies(dependency, toolchain, privatePackageDependencyConsumer,
            publicPackageDependencyConsumer,
            privateProjectDependencyConsumer, publicProjectDependencyConsumer);
      }
    }
  }

  private void resolveLibraryLinking(final Collection<CMakeLibraryLinking> linkDefinitions,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType type,
      final Consumer<String> privateOptionConsumer, final Consumer<String> publicOptionConsumer,
      final Consumer<CMakeResolvedPackageDependency> privatePackageDependencyConsumer,
      final Consumer<CMakeResolvedPackageDependency> publicPackageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> privateProjectDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> publicProjectDependencyConsumer) {
    for (final CMakeLibraryLinking linkDefinition : linkDefinitions) {
      resolveLinkingOptions(linkDefinition.getOptions(), toolchain, privateOptionConsumer, publicOptionConsumer);
      for (final CMakeLibraryDependencies dependency : linkDefinition.getDependencies()) {
        if (Objects.equals(CMakeLinkType.INTERFACE, dependency.getLinkType().orElse(type))
            || Objects.equals(type, dependency.getLinkType().orElse(type))) {
          resolveLinkingDependencies(dependency, toolchain, privatePackageDependencyConsumer,
              publicPackageDependencyConsumer, privateProjectDependencyConsumer, publicProjectDependencyConsumer);
        }
      }
    }
  }

  private void resolveLinkingOptions(final Collection<CMakeBuildItems> linkOptions,
      final CMakeResolvedToolchain toolchain, final Consumer<String> privateOptionConsumer,
      final Consumer<String> publicOptionConsumer) {
    for (final CMakeBuildItems options : linkOptions) {
      if (options.isPrivate()) {
        options.getNames().forEach(privateOptionConsumer);
      } else {
        options.getNames().forEach(publicOptionConsumer);
      }
    }
  }

  private void resolveLinkingDependencies(final CMakeBinaryDependencies dependency,
      final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeResolvedPackageDependency> privatePackageDependencyConsumer,
      final Consumer<CMakeResolvedPackageDependency> publicPackageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> privateProjectDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> publicProjectDependencyConsumer) {
    for (final String name : dependency.getNames()) {
      if (!resolvePackageReference(name, dependency.getFrom(), dependency.isPrivate(),
          privatePackageDependencyConsumer, publicPackageDependencyConsumer)
          && !resolveProjectReference(name, dependency.getFrom(), dependency.getLinkType(), dependency.isPrivate(),
              privateProjectDependencyConsumer, publicProjectDependencyConsumer)) {
        throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(name));
      }
    }
  }

  private boolean resolvePackageReference(final String name, final Optional<String> from, boolean internal,
      final Consumer<CMakeResolvedPackageDependency> privatePackageDependencyConsumer,
      final Consumer<CMakeResolvedPackageDependency> publicPackageDependencyConsumer) {
    if (from.isPresent() && availablePackages.containsKey(from.get())) {
      final CMakePackage availablePackage = availablePackages.get(from.get());
      final CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(availablePackage);
      final CMakeResolvedPackageDependency resolvedDependency = new CMakeResolvedPackageDependency(name,
          resolvedPackage,
          Optional.ofNullable(availablePackage.getTargetPrefix().getOrNull()));
      if (internal) {
        privatePackageDependencyConsumer.accept(resolvedDependency);
      } else {
        publicPackageDependencyConsumer.accept(resolvedDependency);
      }
      return true;
    }
    return false;
  }

  private boolean resolveProjectReference(final String name, final Optional<String> from,
      final Optional<CMakeLinkType> linkage, final boolean internal,
      final Consumer<CMakeResolvedProjectDependency> privateProjectDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> publicProjectDependencyConsumer)
      throws IllegalArgumentException {
    final Project referencedProject = from.isEmpty() || Objects.equals(currentProject.getName(), from.get())
        ? currentProject
        : currentProject.findProject(":%s".formatted(from.get()));
    if (Objects.nonNull(referencedProject)) {
      final CMakeResolvedProject resolvedProject = new CMakeResolvedProject(referencedProject);
      final CMakeResolvedProjectDependency resolvedDependency = new CMakeResolvedProjectDependency(name,
          resolvedProject, linkage);
      if (internal) {
        privateProjectDependencyConsumer.accept(resolvedDependency);
      } else {
        publicProjectDependencyConsumer.accept(resolvedDependency);
      }
      return true;
    }
    return false;
  }

  private void validateLibrary(final CMakeLibrary component) {
    if (component.getHeaders().isEmpty()) {
      throw new IllegalArgumentException(
          "Library '%s' does not have any valid headers configured!".formatted(component.getName()));
    }
  }

  private void validateExecutable(final CMakeBinary component) {
    if (component.getSources().isEmpty()) {
      throw new IllegalArgumentException(
          "Executable '%s' does not have any valid sources configured!".formatted(component.getName()));
    }
  }

}
