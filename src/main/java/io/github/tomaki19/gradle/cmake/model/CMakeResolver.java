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
  private final Collection<CMakeResolvedPackage> availableSystemPackages;
  private final Map<CMakeToolchain, CMakeResolvedToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakeSystemPackage> systemPackages,
      final Set<CMakeToolchain> toolchains) {
    this.project = project;
    this.availableSystemPackages = systemPackages.stream()
        .map(systemPackage -> new CMakeResolvedPackage(systemPackage))
        .toList();
    this.availableToolchains = toolchains.stream()
        .filter((toolchain) -> toolchain.getOperatingSystem().isPresent()
            && Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .collect(Collectors.toMap(Function.identity(), (toolchain) -> new CMakeResolvedToolchain(toolchain)));

  }

  public Collection<CMakeResolvedPackage> getAvailableSystemPackages() {
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
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getLibraries().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getLibraries().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getLibraries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(library.getPrivateLinkDependencies(), resolvedToolchain,
              resolvedLibrary::addPrivateLinkOption, resolvedLibrary::addPrivateSystemPackageDependency,
              resolvedLibrary::addPrivateProjectPackageDependency);
          resolveDependencies(library.getPublicLinkDependencies(), resolvedToolchain,
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
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getApplications().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getApplications().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getApplications().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolveDependencies(application.getPrivateLinkDependencies(), resolvedToolchain,
              resolvedApplication::addPrivateLinkOption, resolvedApplication::addPrivateSystemPackageDependency,
              resolvedApplication::addPrivateProjectPackageDependency);
          resolvedToolchain.addApplication(resolvedApplication);
        }));
  }

  private void resolveTests(final Set<CMakeTest> tests, final Map<CMakeToolchain, CMakeResolvedToolchain> toolchains) {
    tests.forEach((object) -> processObject(object, toolchains,
        (CMakeTest test, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain) -> {
          final CMakeResolvedExecutable resolvedTest = new CMakeResolvedExecutable(test,
              toolchain.getBinaries().getBuildStatic().orElse(Boolean.FALSE)
                  || toolchain.getTests().getBuildStatic().orElse(Boolean.FALSE),
              toolchain.getBinaries().getBuildShared().orElse(Boolean.TRUE)
                  && toolchain.getTests().getBuildShared().orElse(Boolean.TRUE),
              toolchain.getBinaries().getStripDebug().orElse(Boolean.FALSE)
                  || toolchain.getTests().getStripDebug().orElse(Boolean.FALSE),
              toolchain.getBinaries().getPackageBuildOutputs().orElse(Boolean.FALSE)
                  || toolchain.getTests().getPackageBuildOutputs().orElse(Boolean.FALSE));
          resolveDependencies(toolchain.getBinaries().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveDependencies(toolchain.getTests().getPrivateLinkDependencies(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolveDependencies(test.getPrivateLinkDependencies(), resolvedToolchain,
              resolvedTest::addPrivateLinkOption, resolvedTest::addPrivateSystemPackageDependency,
              resolvedTest::addPrivateProjectPackageDependency);
          resolvedToolchain.addTest(resolvedTest);
        }));
  }

  private <U extends CMakeBinary> void processObject(final U binary,
      final Map<CMakeToolchain, CMakeResolvedToolchain> toolchains, final Resolver<U> resolver) {
    toolchains.forEach((toolchain, resolvedToolchain) -> {
      if (binary.getToolchains().contains(toolchain.getName())
          || ((binary instanceof CMakeLibrary) && binary.getToolchains().isEmpty() && binary.getSources().isEmpty())) {
        resolver.resolve(binary, toolchain, resolvedToolchain);
      }
    });
  }

  private interface Resolver<U extends CMakeBinary> {
    void resolve(U binary, CMakeToolchain toolchain, CMakeResolvedToolchain resolvedToolchain);
  }

  private void resolveDependencies(final Collection<String> dependencies, final CMakeResolvedToolchain toolchain,
      final Consumer<String> optionConsumer, final Consumer<String> packageDependencyConsumer,
      final Consumer<CMakeResolvedProjectPackageDependency> moduleDependencyConsumer) {
    for (final String dependency : dependencies) {
      final String[] dependencyTokens = splitDependency(dependency);
      if (dependencyTokens.length == 1) {
        if (dependencyTokens[0].startsWith("-")) {
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

  private String[] splitDependency(final String dependency) {
    String cleanDependency = dependency;
    if (cleanDependency.startsWith("::")) {
      cleanDependency = project.getName() + dependency;
    }
    return cleanDependency.split("::");
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
