/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import java.util.Collection;
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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeSystemPackage;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeTest;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeToolchain;
import io.github.tomaki19.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolver {

  private final Project project;
  private final Collection<CMakeResolvedSystemPackage> availableSystemPackages;
  private final Map<CMakeToolchain, CMakeResolvedToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakeSystemPackage> systemPackages,
      final Set<CMakeToolchain> toolchains) {
    this.project = project;
    this.availableSystemPackages = systemPackages.stream()
        .map(systemPackage -> new CMakeResolvedSystemPackage(systemPackage))
        .toList();
    this.availableToolchains = toolchains.stream()
        .filter(toolchain -> Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .collect(Collectors.toMap(Function.identity(),
            toolchain -> new CMakeResolvedToolchain(toolchain)));
  }

  public Collection<CMakeResolvedSystemPackage> getAvailableSystemPackages() {
    return availableSystemPackages;
  }

  public Collection<CMakeResolvedToolchain> process(final Set<CMakeLibrary> libraries,
      final Set<CMakeApplication> applications, final Set<CMakeTest> tests) {
    resolveLibraries(libraries, availableToolchains);
    resolveApplications(applications, availableToolchains);
    resolveTests(tests, availableToolchains);
    return availableToolchains.values().stream()
        .filter(toolchain -> (toolchain.hasBinaries() || toolchain.hasInterfaceLibraries()))
        .sorted((first, second) -> first.getName().compareTo(second.getName())).toList();
  }

  private void resolveLibraries(final Set<CMakeLibrary> libraries,
      final Map<CMakeToolchain, CMakeResolvedToolchain> build) {
    libraries.forEach((object) -> processObject(object, build,
        (CMakeLibrary library, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
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
      final Map<CMakeToolchain, CMakeResolvedToolchain> toolchains) {
    applications.forEach((object) -> processObject(object, toolchains,
        (CMakeApplication application, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedExecutable resolvedApplication = new CMakeResolvedExecutable(application,
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

  private void resolveTests(final Set<CMakeTest> tests, final Map<CMakeToolchain, CMakeResolvedToolchain> toolchains) {
    tests.forEach((object) -> processObject(object, toolchains,
        (CMakeTest test, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedExecutable resolvedTest = new CMakeResolvedExecutable(test,
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

  private <U extends CMakeBinary> void processObject(final U binary,
      final Map<CMakeToolchain, CMakeResolvedToolchain> toolchains, final Resolver<U> resolver) {
    toolchains.forEach((toolchain, resolvedToolchain) -> {
      if (binary.getToolchains().get().contains(toolchain.getName())
          || ((binary instanceof CMakeLibrary) && binary.getToolchains().get().isEmpty()
              && binary.getSources().get().isEmpty())) {
        resolver.resolve(binary, toolchain, resolvedToolchain);
      }
    });
  }

  private interface Resolver<U extends CMakeBinary> {
    void resolve(U binary, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain);
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
        switch (dependencyTokens.length) {
          case 2:
            packageDependencyConsumer.accept(dependency);
            break;
          case 3:
            resolveProjectPackage(dependencyTokens, moduleDependencyConsumer, toolchain::addModule);
            break;
          default:
            throw new IllegalArgumentException("Invalid link option '%s'!".formatted(dependency));
        }
      }
    }
  }

  private void resolveProjectPackage(final String[] dependencyTokens,
      final Consumer<CMakeResolvedProjectPackageDependency> binaryConsumer,
      final Consumer<CMakeResolvedProject> buildConsumer) throws IllegalArgumentException {
    final Project dependencyProject = Objects.equals(dependencyTokens[0], project.getName()) ? project
        : project.findProject(":%s".formatted(dependencyTokens[0]));
    if (Objects.nonNull(dependencyProject)) {
      final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
      binaryConsumer.accept(new CMakeResolvedProjectPackageDependency(dependencyProject,
          dependencyTokens[1], type));
      buildConsumer.accept(new CMakeResolvedProject(dependencyProject));
    } else {
      throw new IllegalArgumentException("Missing project package '%s'!".formatted(dependencyTokens[0]));
    }
  }

}
