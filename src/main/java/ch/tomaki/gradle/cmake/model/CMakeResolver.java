/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.api.CMakeSystemPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;
import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolver {

  private final Map<String, CMakeToolchain> availableToolchains;
  private final Map<String, CMakeSystemPackage> availableSystemPackges;
  private final Project project;

  public CMakeResolver(final NamedDomainObjectContainer<CMakeToolchain> toolchains,
      final NamedDomainObjectContainer<CMakeSystemPackage> systemPackages, final Project project) {
    this.availableToolchains = toolchains.parallelStream()
        .filter((toolchain) -> Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .collect(Collectors.toMap(CMakeToolchain::getName, Function.identity()));
    this.availableSystemPackges = systemPackages.parallelStream()
        .collect(Collectors.toMap(CMakeSystemPackage::getName, Function.identity()));
    this.project = project;
  }

  public Map<String, CMakeResolvedToolchain> process(final Set<CMakeLibrary> libraries,
      final Set<CMakeApplication> applications, final Set<CMakeTest> tests) {
    final Map<String, CMakeResolvedToolchain> resolvedToolchains = new HashMap<>();
    resolveToolchains(availableToolchains.values(), resolvedToolchains);
    resolveLibraries(libraries, resolvedToolchains);
    resolveApplications(applications, resolvedToolchains);
    resolveTests(tests, resolvedToolchains);
    return resolvedToolchains;
  }

  private void resolveToolchains(final Collection<CMakeToolchain> toolchains,
      Map<String, CMakeResolvedToolchain> resolvedToolchains) {
    toolchains.forEach((toolchain) -> {
      resolvedToolchains.put(toolchain.getName(), new CMakeResolvedToolchain(toolchain));
    });
  }

  private void resolveLibraries(final Set<CMakeLibrary> libraries,
      final Map<String, CMakeResolvedToolchain> resolvedToolchains) {
    libraries.forEach((object) -> processObject(object, (CMakeLibrary library, CMakeToolchain toolchain) -> {
      final CMakeResolvedToolchain resolvedToolchain = resolvedToolchains.get(toolchain.getName());
      final CMakeResolvedLibrary resolvedLibrary = new CMakeResolvedLibrary(library,
          toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE)
              || toolchain.getLibraries().getBuildStatic().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE)
              && toolchain.getLibraries().getBuildShared().getOrElse(Boolean.TRUE),
          toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
              || toolchain.getLibraries().getStripDebug().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
              || toolchain.getLibraries().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
      resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
          resolvedLibrary::addPrivateProjectPackageDependency);
      resolveDependencies(toolchain.getLibraries().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
          resolvedLibrary::addPrivateProjectPackageDependency);
      resolveDependencies(library.getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
          resolvedLibrary::addPrivateProjectPackageDependency);
      resolveDependencies(library.getPublicLinkDependencies().get(), resolvedToolchain,
          resolvedLibrary::addPublicLinkOption, resolvedLibrary::addPublicSystemPackageDependency,
          resolvedLibrary::addPublicProjectPackageDependency);
      resolvedToolchain.addLibrary(resolvedLibrary);
    }));
  }

  private void resolveApplications(final Set<CMakeApplication> applications,
      final Map<String, CMakeResolvedToolchain> resolvedToolchains) {
    applications.forEach((object) -> processObject(object, (CMakeApplication application, CMakeToolchain toolchain) -> {
      final CMakeResolvedToolchain resolvedToolchain = resolvedToolchains.get(toolchain.getName());
      final CMakeResolvedApplication resolvedApplication = new CMakeResolvedApplication(application,
          toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE)
              || toolchain.getApplications().getBuildStatic().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE)
              && toolchain.getApplications().getBuildShared().getOrElse(Boolean.TRUE),
          toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
              || toolchain.getApplications().getStripDebug().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
              || toolchain.getApplications().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
      resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
          resolvedApplication::addPrivateProjectPackageDependency);
      resolveDependencies(toolchain.getApplications().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
          resolvedApplication::addPrivateProjectPackageDependency);
      resolveDependencies(application.getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
          resolvedApplication::addPrivateProjectPackageDependency);
      resolvedToolchain.addApplication(resolvedApplication);
    }));
  }

  private void resolveTests(final Set<CMakeTest> tests, final Map<String, CMakeResolvedToolchain> resolvedToolchains) {
    tests.forEach((object) -> processObject(object, (CMakeTest test, CMakeToolchain toolchain) -> {
      final CMakeResolvedToolchain resolvedToolchain = resolvedToolchains.get(toolchain.getName());
      final CMakeResolvedTest resolvedTest = new CMakeResolvedTest(test,
          toolchain.getBinaries().getBuildStatic().getOrElse(Boolean.FALSE)
              || toolchain.getTests().getBuildStatic().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getBuildShared().getOrElse(Boolean.TRUE)
              && toolchain.getTests().getBuildShared().getOrElse(Boolean.TRUE),
          toolchain.getBinaries().getStripDebug().getOrElse(Boolean.FALSE)
              || toolchain.getTests().getStripDebug().getOrElse(Boolean.FALSE),
          toolchain.getBinaries().getPackageBuildOutputs().getOrElse(Boolean.FALSE)
              || toolchain.getTests().getPackageBuildOutputs().getOrElse(Boolean.FALSE));
      resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
          resolvedTest::addPrivateProjectPackageDependency);
      resolveDependencies(toolchain.getTests().getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
          resolvedTest::addPrivateProjectPackageDependency);
      resolveDependencies(test.getPrivateLinkDependencies().get(), resolvedToolchain,
          resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
          resolvedTest::addPrivateProjectPackageDependency);
      resolvedToolchain.addTest(resolvedTest);
    }));
  }

  private <B extends CMakeBinary> void processObject(final B binary, final Resolver<B> resolver) {
    availableToolchains.forEach((toolchainName, toolchain) -> {
      if (binary.getToolchains().get().contains(toolchainName)
          || ((binary instanceof CMakeLibrary) && binary.getToolchains().get().isEmpty()
              && binary.getSources().get().isEmpty())) {
        resolver.resolve(binary, toolchain);
      }
    });
  }

  private interface Resolver<B extends CMakeBinary> {
    void resolve(B binary, CMakeToolchain toolchain);
  }

  private void resolveDependencies(final Set<String> dependencies, final CMakeResolvedToolchain toolchain,
      final Consumer<String> optionConsumer, final Consumer<String> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectPackageDependency> moduleDependencyConsumer) {
    for (final String dependency : dependencies) {
      final String[] dependencyTokens = dependency.split("::");
      if (dependency.startsWith("-")) {
        if (dependencyTokens.length == 1) {
          optionConsumer.accept(dependencyTokens[0]);
        } else {
          throw new IllegalArgumentException("Invalid link option: '%s'!".formatted(dependencyTokens[0]));
        }
      } else {
        if (dependencyTokens.length <= 2) {
          resolvePackage(dependencyTokens, dependency, packageDependencyConsumer, toolchain::addPackage);
        } else if (dependencyTokens.length == 3) {
          resolveModule(dependencyTokens, toolchain, moduleDependencyConsumer, toolchain::addModule);
        } else {
          throw new IllegalArgumentException("Missing dependency '%s'!".formatted(dependency));
        }
      }
    }
  }

  private void resolvePackage(final String[] dependencyTokens, final String dependency,
      final Consumer<String> binaryConsumer, final Consumer<CMakeResolvedSystemPackage> buildConsumer)
      throws IllegalArgumentException {
    final CMakeSystemPackage systemPackage = availableSystemPackges.get(dependencyTokens[0]);
    if (Objects.nonNull(systemPackage)) {
      binaryConsumer.accept(dependency);
      buildConsumer.accept(new CMakeResolvedSystemPackage(systemPackage));
    } else {
      throw new IllegalArgumentException("Missing package '%s'!".formatted(dependencyTokens[0]));
    }
  }

  private void resolveModule(final String[] dependencyTokens, final CMakeResolvedToolchain toolchain,
      final Consumer<CMakeResolvedProjectPackageDependency> binaryConsumer,
      final Consumer<CMakeResolvedProjectPackage> buildConsumer) throws IllegalArgumentException {
    final Project dependencyProject = Objects.equals(dependencyTokens[0], project.getName()) ? project
        : project.findProject(":%s".formatted(dependencyTokens[0]));
    if (Objects.nonNull(dependencyProject)) {
      final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
      binaryConsumer
          .accept(new CMakeResolvedProjectPackageDependency(dependencyTokens[1], toolchain, type, dependencyProject));
      buildConsumer.accept(new CMakeResolvedProjectPackage(toolchain, dependencyProject));
    } else {
      throw new IllegalArgumentException("Missing module '%s'!".formatted(dependencyTokens[0]));
    }
  }

}
