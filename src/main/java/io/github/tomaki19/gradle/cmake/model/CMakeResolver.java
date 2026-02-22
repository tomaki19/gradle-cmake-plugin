/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
          final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(component,
              toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE));
          final Set<CMakeLinkType> libraryTypes = new HashSet<>();
          resolveCompiling(toolchain.getLibraries().getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(component.getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(component.getPublicCompile(),
              resolvedLibrary::addPublicCompileDefinitions,
              resolvedLibrary::addPublicCompileOptions);
          resolveLinkingOptions(toolchain.getLibraries().getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption);
          resolveLinkingOptions(component.getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption);
          resolveLinkingOptions(component.getPublicLinking(), resolvedToolchain,
              resolvedLibrary::addPublicLinkOption);
          resolveLinking(toolchain.getLibraries().getPrivateLinking(), resolvedToolchain, libraryTypes::add,
              resolvedLibrary::addPrivatePackageDependency,
              resolvedLibrary::addPrivateProjectDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain, libraryTypes::add,
              resolvedLibrary::addPrivatePackageDependency,
              resolvedLibrary::addPrivateProjectDependency);
          resolveLinking(component.getPublicLinking(), resolvedToolchain, libraryTypes::add,
              resolvedLibrary::addPublicPackageDependency,
              resolvedLibrary::addPublicProjectDependency);
          if (component.getSources().isEmpty()) {
            resolvedToolchain.addInterfaceLibrary(resolvedLibrary);
          } else {
            if (toolchain.getLibraries().getBuildStatic().getOrElse(Boolean.FALSE)
                || component.getBuildStatic().getOrElse(Boolean.FALSE) ||
                libraryTypes.contains(CMakeLinkType.STATIC)) {
              resolvedToolchain.addStaticLibrary(resolvedLibrary);
            }
            if (toolchain.getLibraries().getBuildShared().getOrElse(Boolean.TRUE)
                && component.getBuildShared().getOrElse(Boolean.TRUE) ||
                libraryTypes.contains(CMakeLinkType.SHARED)) {
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
          resolveCompiling(component.getPrivateCompile(),
              resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveLinkingOptions(toolchain.getApplications().getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption);
          resolveLinkingOptions(component.getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption);
          resolveLinking(toolchain.getApplications().getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivatePackageDependency,
              resolvedApplication::addPrivateProjectDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivatePackageDependency,
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
          resolveCompiling(component.getPrivateCompile(),
              resolvedTest::addPrivateCompileDefinitions,
              resolvedTest::addPrivateCompileOptions);
          resolveLinkingOptions(toolchain.getTests().getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption);
          resolveLinkingOptions(component.getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption);
          resolveLinking(toolchain.getTests().getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivatePackageDependency,
              resolvedTest::addPrivateProjectDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivatePackageDependency,
              resolvedTest::addPrivateProjectDependency);
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

  private void resolveCompiling(final CMakeCompile compiling, final Consumer<String> definitionConsumer,
      final Consumer<String> optionConsumer) {
    for (final String define : compiling.getDefines()) {
      definitionConsumer.accept(define);
    }
    for (final String option : compiling.getOptions()) {
      optionConsumer.accept(option);
    }
  }

  private void resolveLinkingOptions(final CMakeLinking linking, final CMakeResolvedToolchain toolchain,
      final Consumer<String> optionConsumer) {
    for (final String option : linking.getOptions()) {
      optionConsumer.accept(option);
    }
  }

  private void resolveLinking(final CMakeExecutableLinking linking, final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeResolvedPackageDependency> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final CMakeExecutableDependencies dependency : linking.getDependencies()) {
      for (final String name : dependency.getNames()) {
        if (!resolvePackageReference(name, dependency.getFrom(), toolchain::addPackage, packageDependencyConsumer)
            && !resolveProjectReference(name, dependency.getFrom(), dependency.getLinkage(), toolchain::addProject,
                projectDependencyConsumer)) {
          throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(name));
        }
      }
    }
  }

  private void resolveLinking(final CMakeLibraryLinking linking, final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeLinkType> libraryTypeConsumer,
      final Consumer<CMakeResolvedPackageDependency> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final CMakeLibraryDependencies dependency : linking.getDependencies()) {
      for (final String name : dependency.getNames()) {
        if (!resolvePackageReference(name, dependency.getFrom(), toolchain::addPackage, packageDependencyConsumer)
            && !resolveProjectReference(name, dependency.getFrom(), dependency.getLinkage(), toolchain::addProject,
                projectDependencyConsumer)) {
          throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(name));
        }
      }
      dependency.getBuildType().ifPresent((buildType) -> {
        libraryTypeConsumer.accept(buildType);
      });
    }
  }

  private boolean resolvePackageReference(final String name, final Optional<String> from,
      final Consumer<CMakeResolvedPackage> toolchainConsumer,
      final Consumer<CMakeResolvedPackageDependency> objectConsumer) {
    if (from.isPresent()
        && availablePackages.containsKey(from.get())) {
      final CMakePackage availablePackage = availablePackages.get(from.get());
      final CMakeResolvedPackage resolvedPackage = new CMakeResolvedPackage(availablePackage);
      toolchainConsumer.accept(resolvedPackage);
      objectConsumer.accept(new CMakeResolvedPackageDependency(name, resolvedPackage,
          Optional.ofNullable(availablePackage.getTargetPrefix().getOrNull())));
      return true;
    }
    return false;
  }

  private boolean resolveProjectReference(final String name, final Optional<String> from,
      final Optional<CMakeLinkType> linkage,
      final Consumer<CMakeResolvedProject> toolchainConsumer,
      final Consumer<CMakeResolvedProjectDependency> objectConsumer)
      throws IllegalArgumentException {
    final Project referencedProject = from.isEmpty() || Objects.equals(currentProject.getName(), from.get())
        ? currentProject
        : currentProject.findProject(":%s".formatted(from.get()));
    if (Objects.nonNull(referencedProject)) {
      final CMakeResolvedProject resolvedProject = new CMakeResolvedProject(referencedProject);
      if (!Objects.equals(currentProject.getName(), referencedProject.getName())) {
        toolchainConsumer.accept(resolvedProject);
      }
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
