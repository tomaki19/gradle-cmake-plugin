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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildType;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLinking;
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
            resolveCompiling(Arrays.asList(toolchain.getLibraries().getPrivateCompile(),
                component.getPrivateCompile()), resolvedLibrary::addPrivateCompileDefinitions,
                resolvedLibrary::addPrivateCompileOptions);
            resolveCompiling(Arrays.asList(component.getPublicCompile()),
                resolvedLibrary::addPublicCompileDefinitions, resolvedLibrary::addPublicCompileOptions);
            resolveLinkingOptions(Arrays.asList(toolchain.getLibraries().getPrivateLinking(),
                component.getPrivateLinking()), resolvedToolchain, resolvedLibrary::addPrivateLinkOption);
            resolveLinkingOptions(Arrays.asList(component.getPublicLinking()), resolvedToolchain,
                resolvedLibrary::addPublicLinkOption);
            resolveLinking(Arrays.asList(toolchain.getLibraries().getPrivateLinking(), component.getPrivateLinking()),
                resolvedToolchain, CMakeLinkType.INTERFACE, resolvedLibrary::addPrivatePackageDependency,
                resolvedLibrary::addPrivateProjectDependency);
            resolveLinking(Arrays.asList(component.getPublicLinking()), resolvedToolchain, CMakeLinkType.INTERFACE,
                resolvedLibrary::addPublicPackageDependency, resolvedLibrary::addPublicProjectDependency);
            resolvedToolchain.addInterfaceLibrary(resolvedLibrary);
          } else {
            if (toolchain.getLibraries().getBuildTypes().get().contains(CMakeBuildType.STATIC)
                || component.getBuildTypes().get().contains(CMakeBuildType.STATIC)) {
              final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component, CMakeLinkType.STATIC,
                  toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
              resolveCompiling(Arrays.asList(toolchain.getLibraries().getPrivateCompile(),
                  component.getPrivateCompile()), resolvedLibrary::addPrivateCompileDefinitions,
                  resolvedLibrary::addPrivateCompileOptions);
              resolveCompiling(Arrays.asList(component.getPublicCompile()),
                  resolvedLibrary::addPublicCompileDefinitions, resolvedLibrary::addPublicCompileOptions);
              resolveLinkingOptions(Arrays.asList(toolchain.getLibraries().getPrivateLinking(),
                  component.getPrivateLinking()), resolvedToolchain, resolvedLibrary::addPrivateLinkOption);
              resolveLinkingOptions(Arrays.asList(component.getPublicLinking()), resolvedToolchain,
                  resolvedLibrary::addPublicLinkOption);
              resolveLinking(Arrays.asList(toolchain.getLibraries().getPrivateLinking(), component.getPrivateLinking()),
                  resolvedToolchain, CMakeLinkType.STATIC, resolvedLibrary::addPrivatePackageDependency,
                  resolvedLibrary::addPrivateProjectDependency);
              resolveLinking(Arrays.asList(component.getPublicLinking()), resolvedToolchain, CMakeLinkType.STATIC,
                  resolvedLibrary::addPublicPackageDependency, resolvedLibrary::addPublicProjectDependency);
              resolvedToolchain.addStaticLibrary(resolvedLibrary);
            }
            if ((toolchain.getLibraries().getBuildTypes().get().isEmpty()
                || toolchain.getLibraries().getBuildTypes().get().contains(CMakeBuildType.SHARED))
                && (component.getBuildTypes().get().isEmpty()
                    || component.getBuildTypes().get().contains(CMakeBuildType.SHARED))) {
              final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component, CMakeLinkType.SHARED,
                  toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
              resolveCompiling(Arrays.asList(toolchain.getLibraries().getPrivateCompile(),
                  component.getPrivateCompile()), resolvedLibrary::addPrivateCompileDefinitions,
                  resolvedLibrary::addPrivateCompileOptions);
              resolveCompiling(Arrays.asList(component.getPublicCompile()),
                  resolvedLibrary::addPublicCompileDefinitions, resolvedLibrary::addPublicCompileOptions);
              resolveLinkingOptions(Arrays.asList(toolchain.getLibraries().getPrivateLinking(),
                  component.getPrivateLinking()), resolvedToolchain, resolvedLibrary::addPrivateLinkOption);
              resolveLinkingOptions(Arrays.asList(component.getPublicLinking()), resolvedToolchain,
                  resolvedLibrary::addPublicLinkOption);
              resolveLinking(Arrays.asList(toolchain.getLibraries().getPrivateLinking(), component.getPrivateLinking()),
                  resolvedToolchain, CMakeLinkType.SHARED, resolvedLibrary::addPrivatePackageDependency,
                  resolvedLibrary::addPrivateProjectDependency);
              resolveLinking(Arrays.asList(component.getPublicLinking()), resolvedToolchain, CMakeLinkType.SHARED,
                  resolvedLibrary::addPublicPackageDependency, resolvedLibrary::addPublicProjectDependency);
              resolvedToolchain.addSharedLibrary(resolvedLibrary);
            }
          }
        }));
  }

  private void resolveApplications(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeApplication> applications) {
    applications.forEach((component) -> processObject(component, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          validateExecutable(component);
          final CMakeResolvedExecutable resolvedApplication = new CMakeResolvedExecutable(component,
              toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE));
          resolveCompiling(Arrays.asList(toolchain.getApplications().getPrivateCompile(),
              component.getPrivateCompile()), resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveLinkingOptions(Arrays.asList(toolchain.getApplications().getPrivateLinking(),
              component.getPrivateLinking()), resolvedToolchain, resolvedApplication::addPrivateLinkOption);
          resolveLinking(Arrays.asList(toolchain.getApplications().getPrivateLinking(), component.getPrivateLinking()),
              resolvedToolchain, resolvedApplication::addPrivatePackageDependency,
              resolvedApplication::addPrivateProjectDependency);
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
          resolveCompiling(Arrays.asList(toolchain.getTests().getPrivateCompile(), component.getPrivateCompile()),
              resolvedTest::addPrivateCompileDefinitions, resolvedTest::addPrivateCompileOptions);
          resolveLinkingOptions(Arrays.asList(toolchain.getTests().getPrivateLinking(),
              component.getPrivateLinking()), resolvedToolchain, resolvedTest::addPrivateLinkOption);
          resolveLinking(Arrays.asList(toolchain.getTests().getPrivateLinking(), component.getPrivateLinking()),
              resolvedToolchain, resolvedTest::addPrivatePackageDependency, resolvedTest::addPrivateProjectDependency);
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

  private void resolveCompiling(final Collection<CMakeCompile> compileDefinitions,
      final Consumer<String> definitionConsumer, final Consumer<String> optionConsumer) {
    for (final CMakeCompile compileDefinition : compileDefinitions) {
      for (final String define : compileDefinition.getDefines()) {
        definitionConsumer.accept(define);
      }
      for (final String option : compileDefinition.getOptions()) {
        optionConsumer.accept(option);
      }
    }
  }

  private void resolveLinkingOptions(final Collection<CMakeLinking> linkDefinitions,
      final CMakeResolvedToolchain toolchain,
      final Consumer<String> optionConsumer) {
    for (final CMakeLinking linkDefinition : linkDefinitions) {
      for (final String option : linkDefinition.getOptions()) {
        optionConsumer.accept(option);
      }
    }
  }

  private void resolveLinking(final Collection<CMakeExecutableLinking> linkDefinitions,
      final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeResolvedPackageDependency> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final CMakeExecutableLinking linkDefinition : linkDefinitions) {
      for (final CMakeExecutableDependencies dependency : linkDefinition.getDependencies()) {
        resolveLinking(dependency, toolchain, packageDependencyConsumer, projectDependencyConsumer);
      }
    }
  }

  private void resolveLinking(final Collection<CMakeLibraryLinking> linkDefinitions,
      final CMakeResolvedToolchain toolchain, final CMakeLinkType type,
      final Consumer<CMakeResolvedPackageDependency> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final CMakeLibraryLinking linkDefinition : linkDefinitions) {
      for (final CMakeLibraryDependencies dependency : linkDefinition.getDependencies()) {
        if (Objects.equals(CMakeLinkType.INTERFACE, dependency.getLinkType().orElse(type))
            || Objects.equals(type, dependency.getLinkType().orElse(type))) {
          resolveLinking(dependency, toolchain, packageDependencyConsumer, projectDependencyConsumer);
        }
      }
    }
  }

  private void resolveLinking(final CMakeBinaryDependencies dependency, final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeResolvedPackageDependency> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final String name : dependency.getNames()) {
      if (!resolvePackageReference(name, dependency.getFrom(), packageDependencyConsumer)
          && !resolveProjectReference(name, dependency.getFrom(), dependency.getLinkType(),
              projectDependencyConsumer)) {
        throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(name));
      }
    }
  }

  private boolean resolvePackageReference(final String name, final Optional<String> from,
      final Consumer<CMakeResolvedPackageDependency> objectConsumer) {
    if (from.isPresent() && availablePackages.containsKey(from.get())) {
      final CMakePackage availablePackage = availablePackages.get(from.get());
      final CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(availablePackage);
      objectConsumer.accept(new CMakeResolvedPackageDependency(name, resolvedPackage,
          Optional.ofNullable(availablePackage.getTargetPrefix().getOrNull())));
      return true;
    }
    return false;
  }

  private boolean resolveProjectReference(final String name, final Optional<String> from,
      final Optional<CMakeLinkType> linkage, final Consumer<CMakeResolvedProjectDependency> objectConsumer)
      throws IllegalArgumentException {
    final Project referencedProject = from.isEmpty() || Objects.equals(currentProject.getName(), from.get())
        ? currentProject
        : currentProject.findProject(":%s".formatted(from.get()));
    if (Objects.nonNull(referencedProject)) {
      final CMakeResolvedProject resolvedProject = new CMakeResolvedProject(referencedProject);
      objectConsumer.accept(new CMakeResolvedProjectDependency(name, resolvedProject, linkage));
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
