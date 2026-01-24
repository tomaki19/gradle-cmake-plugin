/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCompile;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeDependencies;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLinking;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;

public final class CMakeResolver {

  private final Project currentProject;
  private final Map<String, CMakePackage> availablePackages;
  private final Map<String, CMakeToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakePackage> packages, final Set<CMakeToolchain> toolchains) {
    this.currentProject = project;
    this.availablePackages = packages.stream()
        .collect(Collectors.toUnmodifiableMap(CMakePackage::getName, Function.identity()));
    this.availableToolchains = toolchains.stream()
        .filter((toolchain) -> toolchain.getOperatingSystem().isPresent()
            && Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
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
              toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
                  || toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
                  || toolchain.getLibraries().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(toolchain.getLibraries().getPrivateCompile(),
              resolvedLibrary::addPublicCompileDefinitions,
              resolvedLibrary::addPublicCompileOptions);
          resolveCompiling(component.getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(component.getPublicCompile(),
              resolvedLibrary::addPublicCompileDefinitions,
              resolvedLibrary::addPublicCompileOptions);
          resolveLinking(toolchain.getLibraries().getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveLinking(component.getPublicLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          if (component.getSources().isEmpty()) {
            resolveLinking(toolchain.getLibraries().getPrivateInterfaceLinking(), resolvedToolchain,
                resolvedLibrary::addPrivateLinkOption,
                resolvedLibrary::addPrivateSystemPackageDependency,
                resolvedLibrary::addPrivateProjectPackageDependency);
            resolveLinking(component.getPrivateInterfaceLinking(), resolvedToolchain,
                resolvedLibrary::addPrivateLinkOption,
                resolvedLibrary::addPrivateSystemPackageDependency,
                resolvedLibrary::addPrivateProjectPackageDependency);
            resolveLinking(component.getPublicInterfaceLinking(), resolvedToolchain,
                resolvedLibrary::addPublicLinkOption,
                resolvedLibrary::addPublicSystemPackageDependency,
                resolvedLibrary::addPublicProjectPackageDependency);
            resolvedToolchain.addInterfaceLibrary(resolvedLibrary);
          } else {
            if (toolchain.getLibraries().getBuildStatic().getOrElse(Boolean.FALSE)
                || component.getBuildStatic().getOrElse(Boolean.FALSE)) {
              resolveLinking(toolchain.getLibraries().getPrivateStaticLinking(), resolvedToolchain,
                  resolvedLibrary::addPrivateLinkOption,
                  resolvedLibrary::addPrivateSystemPackageDependency,
                  resolvedLibrary::addPrivateProjectPackageDependency);
              resolveLinking(component.getPrivateStaticLinking(), resolvedToolchain,
                  resolvedLibrary::addPrivateLinkOption,
                  resolvedLibrary::addPrivateSystemPackageDependency,
                  resolvedLibrary::addPrivateProjectPackageDependency);
              resolveLinking(component.getPublicStaticLinking(), resolvedToolchain,
                  resolvedLibrary::addPublicLinkOption,
                  resolvedLibrary::addPublicSystemPackageDependency,
                  resolvedLibrary::addPublicProjectPackageDependency);
              resolvedToolchain.addStaticLibrary(resolvedLibrary);
            }
            if (toolchain.getLibraries().getBuildShared().getOrElse(Boolean.TRUE)
                && component.getBuildShared().getOrElse(Boolean.TRUE)) {
              resolveLinking(toolchain.getLibraries().getPrivateSharedLinking(), resolvedToolchain,
                  resolvedLibrary::addPrivateLinkOption,
                  resolvedLibrary::addPrivateSystemPackageDependency,
                  resolvedLibrary::addPrivateProjectPackageDependency);
              resolveLinking(component.getPrivateSharedLinking(), resolvedToolchain,
                  resolvedLibrary::addPrivateLinkOption,
                  resolvedLibrary::addPrivateSystemPackageDependency,
                  resolvedLibrary::addPrivateProjectPackageDependency);
              resolveLinking(component.getPublicSharedLinking(), resolvedToolchain,
                  resolvedLibrary::addPublicLinkOption,
                  resolvedLibrary::addPublicSystemPackageDependency,
                  resolvedLibrary::addPublicProjectPackageDependency);
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
              toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
                  || toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
                  || toolchain.getApplications().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveCompiling(component.getPrivateCompile(),
              resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveLinking(toolchain.getApplications().getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);

          resolvedToolchain.addApplication(resolvedApplication);
        }));
  }

  private void resolveTests(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeTest> tests) {
    tests.forEach((component) -> processObject(component, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          validateExecutable(component);
          final CMakeResolvedExecutable resolvedTest = new CMakeResolvedExecutable(component,
              toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
                  || toolchain.getTests().getStripDebug().getOrElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
                  || toolchain.getTests().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedTest::addPrivateCompileDefinitions,
              resolvedTest::addPrivateCompileOptions);
          resolveCompiling(component.getPrivateCompile(),
              resolvedTest::addPrivateCompileDefinitions,
              resolvedTest::addPrivateCompileOptions);
          resolveLinking(toolchain.getTests().getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveLinking(component.getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
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

  private void resolveLinking(final CMakeLinking linking, final CMakeResolvedToolchain toolchain,
      final Consumer<String> optionConsumer, final Consumer<String> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final String option : linking.getOptions()) {
      optionConsumer.accept(option);
    }
    for (final CMakeDependencies dependency : linking.getDependencies()) {
      for (final String name : dependency.getNames()) {
        if (!resolvePackageReference(name, dependency.getFrom(), packageDependencyConsumer, toolchain::addPackage)
            && !resolveProjectReference(name, dependency.getFrom(), dependency.getLinkage(), projectDependencyConsumer,
                toolchain::addProject)) {
          throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(name));
        }
      }
    }
  }

  private boolean resolvePackageReference(final String name, final Optional<String> from,
      final Consumer<String> cmakeConsumer, final Consumer<CMakeResolvedPackage> toolchainConsumer) {
    if (from.isPresent()
        && availablePackages.containsKey(from.get())) {
      final CMakePackage availablePackage = availablePackages.get(from.get());
      final String targetPrefix = availablePackage.getTargetPrefix().orElse(availablePackage.getName());
      cmakeConsumer.accept("%s::%s".formatted(targetPrefix, name));
      toolchainConsumer.accept(new CMakeResolvedPackage(availablePackage));
      return true;
    }
    return false;
  }

  private boolean resolveProjectReference(final String name, final Optional<String> from,
      final Optional<CMakeLinkage> linkage, final Consumer<CMakeResolvedProjectDependency> cmakeConsumer,
      final Consumer<CMakeResolvedProject> toolchainConsumer) throws IllegalArgumentException {
    final Project referencedProject = from.isEmpty() || Objects.equals(currentProject.getName(), from.get())
        ? currentProject
        : currentProject.findProject(":%s".formatted(from.get()));
    if (Objects.nonNull(referencedProject)) {
      cmakeConsumer.accept(new CMakeResolvedProjectDependency(referencedProject, name, linkage));
      if (!Objects.equals(currentProject.getName(), referencedProject.getName())) {
        toolchainConsumer.accept(new CMakeResolvedProject(referencedProject));
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
