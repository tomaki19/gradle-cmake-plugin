/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import io.github.tomaki19.gradle.cmake.extension.api.CMakeApplication;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBinary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibrary;
import io.github.tomaki19.gradle.cmake.extension.api.CMakePackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolver {

  private final Project currentProject;
  private final Map<String, CMakePackage> availablePackages;
  private final Map<String, CMakeToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakePackage> packages, final Set<CMakeToolchain> toolchains) {
    this.currentProject = project;
    this.availablePackages = new HashMap<>();
    packages.forEach((object) -> {
      availablePackages.put(object.getName(), object);
      object.getTargetPrefix().ifPresent((targetPrefix) -> {
        availablePackages.put(targetPrefix, object);
      });
    });
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
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getLibraries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(object.getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption,
              resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(object.getPublicLinkDependencies(), resolvedToolchain,
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
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getApplications().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption,
              resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveDependencies(object.getPrivateLinkDependencies(), resolvedToolchain,
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
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getTests().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption,
              resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveDependencies(object.getPrivateLinkDependencies(), resolvedToolchain,
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

  private void resolveDependencies(final Collection<String> dependencies,
      final CMakeResolvedToolchain toolchain, final Consumer<String> optionConsumer,
      final Consumer<String> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectDependency> projectDependencyConsumer) {
    for (final String dependency : dependencies) {
      final String[] dependencyTokens = dependency.split("::");
      switch (dependencyTokens.length) {
        case 1:
          optionConsumer.accept(dependencyTokens[0]);
          break;
        case 2:
          resolvePackageReference(dependencyTokens, packageDependencyConsumer, toolchain::addPackage);
          break;
        case 3:
          resolveProjectReference(dependencyTokens, projectDependencyConsumer, toolchain::addProject);
          break;
        default:
          throw new IllegalArgumentException("Invalid link option '%s'!".formatted(dependency));
      }
    }
  }

  private void resolvePackageReference(final String[] dependencyTokens,
      final Consumer<String> cmakeConsumer,
      final Consumer<CMakeResolvedPackage> toolchainConsumer) {
    if (availablePackages.containsKey(dependencyTokens[0])) {
      final CMakePackage availablePackage = availablePackages.get(dependencyTokens[0]);
      cmakeConsumer.accept("%s::%s".formatted(availablePackage.getName(), dependencyTokens[1]));
      toolchainConsumer.accept(new CMakeResolvedPackage(availablePackage));
    } else {
      throw new IllegalArgumentException("Missing referenced package '%s'!".formatted(dependencyTokens[0]));
    }
  }

  private void resolveProjectReference(final String[] dependencyTokens,
      final Consumer<CMakeResolvedProjectDependency> cmakeConsumer,
      final Consumer<CMakeResolvedProject> toolchainConsumer) throws IllegalArgumentException {
    final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
    if (dependencyTokens[0].isEmpty() || Objects.equals(currentProject.getName(), dependencyTokens[0])) {
      cmakeConsumer.accept(new CMakeResolvedProjectDependency(currentProject, dependencyTokens[1], type));
    } else {
      final Project referencedProject = currentProject.findProject(":%s".formatted(dependencyTokens[0]));
      if (Objects.nonNull(referencedProject)) {
        cmakeConsumer.accept(new CMakeResolvedProjectDependency(referencedProject, dependencyTokens[1], type));
        toolchainConsumer.accept(new CMakeResolvedProject(referencedProject));
      } else {
        throw new IllegalArgumentException("Missing referenced project '%s'!".formatted(dependencyTokens[0]));
      }
    }
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
