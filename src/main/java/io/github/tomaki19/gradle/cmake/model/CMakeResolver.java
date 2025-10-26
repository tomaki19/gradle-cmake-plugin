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
    libraries.forEach((object) -> processObject(object, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(object,
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getLibraries().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedLibrary::addPublicCompileDefinitions,
              resolvedLibrary::addPublicCompileOptions);
          resolveCompiling(object.getPrivateCompile(),
              resolvedLibrary::addPrivateCompileDefinitions,
              resolvedLibrary::addPrivateCompileOptions);
          resolveCompiling(object.getPrivateCompile(),
              resolvedLibrary::addPublicCompileDefinitions,
              resolvedLibrary::addPublicCompileOptions);
          resolveLinking(toolchain.getBinaries().getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveLinking(toolchain.getLibraries().getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveLinking(object.getPrivateLinking(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveLinking(object.getPublicLinking(), resolvedToolchain,
              resolvedLibrary::addPublicLinkOption,
              resolvedLibrary::addPublicSystemPackageDependency,
              resolvedLibrary::addPublicProjectPackageDependency);
          validateLibrary(resolvedLibrary);
          resolvedToolchain.addLibrary(resolvedLibrary);
        }));
  }

  private void resolveApplications(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeApplication> applications) {
    applications.forEach((object) -> processObject(object, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedExecutable resolvedApplication = new CMakeResolvedExecutable(object,
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getApplications().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveCompiling(object.getPrivateCompile(),
              resolvedApplication::addPrivateCompileDefinitions,
              resolvedApplication::addPrivateCompileOptions);
          resolveLinking(toolchain.getBinaries().getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveLinking(toolchain.getApplications().getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveLinking(object.getPrivateLinking(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          validateExecutable(resolvedApplication);
          resolvedToolchain.addApplication(resolvedApplication);
        }));
  }

  private void resolveTests(final Map<String, CMakeResolvedToolchain> resolvedToolchains,
      final Map<String, CMakeToolchain> availableToolchains, final Set<CMakeTest> tests) {
    tests.forEach((object) -> processObject(object, availableToolchains, resolvedToolchains,
        (CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedExecutable resolvedTest = new CMakeResolvedExecutable(object,
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getTests().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getTests().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getTests().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getTests().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveCompiling(toolchain.getBinaries().getPrivateCompile(),
              resolvedTest::addPrivateCompileDefinitions,
              resolvedTest::addPrivateCompileOptions);
          resolveCompiling(object.getPrivateCompile(),
              resolvedTest::addPrivateCompileDefinitions,
              resolvedTest::addPrivateCompileOptions);
          resolveLinking(toolchain.getBinaries().getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveLinking(toolchain.getTests().getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveLinking(object.getPrivateLinking(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          validateExecutable(resolvedTest);
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

  private void validateLibrary(final CMakeResolvedLibrary component) {
    if (component.getHeaders().isEmpty()) {
      throw new IllegalArgumentException("Library '%s' does not provide any headers!".formatted(component.getName()));
    }
  }

  private void validateExecutable(final CMakeResolvedExecutable component) {
    if (component.getSources().isEmpty()) {
      throw new IllegalArgumentException("Executable '%s' has no sources to build!".formatted(component.getName()));
    }
  }

}
