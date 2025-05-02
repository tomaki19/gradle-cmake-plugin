/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.CMakeObject;
import ch.tomaki.gradle.cmake.extension.CMakeTest;
import ch.tomaki.gradle.cmake.extension.CMakeToolchain;

public final class CMakeResolver {

  private final Project project;
  private final Map<String, CMakeFindPackage> availableFindPackages;
  private final Map<String, CMakeToolchain> availableToolchains;

  public CMakeResolver(final Project project, final Set<CMakeFindPackage> findPackages,
      final Set<CMakeToolchain> toolchains) {
    this.project = project;
    this.availableFindPackages = findPackages.parallelStream()
        .collect(Collectors.toMap(CMakeFindPackage::getName, Function.identity()));
    this.availableToolchains = toolchains.parallelStream()
        .filter((toolchain) -> Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .collect(Collectors.toMap(CMakeToolchain::getName, Function.identity()));
  }

  public CMakeResolvedBuild process(final Set<CMakeLibrary> libraries, final Set<CMakeApplication> applications,
      final Set<CMakeTest> tests) {
    final CMakeResolvedBuild build = new CMakeResolvedBuild();
    processLibraries(build, libraries);
    processApplications(build, applications);
    processTests(build, tests);
    return build;
  }

  private void processLibraries(final CMakeResolvedBuild build, final Set<CMakeLibrary> libraries) {
    process(build, libraries,
        (CMakeLibrary library, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedLibrary(library, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedLibrary library) -> {
          build.addToolchain(library.getResolvedToolchain());
          build.addFindPackages(library.getPrivateFindPackages());
          build.addFindPackages(library.getPublicFindPackages());
          build.addProjectModules(library.getPrivateProjectModules());
          build.addProjectModules(library.getPublicProjectModules());
          build.add(library);
        });
  }

  private void processApplications(final CMakeResolvedBuild build, final Set<CMakeApplication> applications) {
    process(build, applications,
        (CMakeApplication application, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedApplication(application, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedApplication application) -> {
          build.addToolchain(application.getResolvedToolchain());
          build.addFindPackages(application.getPrivateFindPackages());
          build.addProjectModules(application.getPrivateProjectModules());
          build.add(application);
        });
  }

  private void processTests(final CMakeResolvedBuild build, final Set<CMakeTest> tests) {
    process(build, tests,
        (CMakeTest test, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedTest(test, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedTest test) -> {
          build.addToolchain(test.getResolvedToolchain());
          build.addFindPackages(test.getPrivateFindPackages());
          build.addProjectModules(test.getPrivateProjectModules());
          build.add(test);
        });
  }

  private <O extends CMakeObject, R extends CMakeResolvedBinary> void process(final CMakeResolvedBuild resolvedBuild,
      final Set<O> cmakeObjects, final Resolver<O, R> resolver, final Acceptor<R> acceptor) {
    cmakeObjects.parallelStream().forEach((cmakeObject) -> {
      cmakeObject.getToolchains().get().forEach((toolchainName) -> {
        Optional.ofNullable(availableToolchains.get(toolchainName)).ifPresent((toolchain) -> {
          toolchain.getBuildConfigs().get().forEach((buildConfig) -> {
            final R resolvedBinary = resolver.resolve(cmakeObject, toolchain, buildConfig);
            acceptor.accept(resolvedBinary);
          });
        });
      });
      if (cmakeObject.getToolchains().get().isEmpty()) {
        resolvedBuild.add(new CMakeResolvedInterface(cmakeObject));
      }
    });
  }

  private interface Resolver<O extends CMakeObject, R extends CMakeResolvedBinary> {
    R resolve(final O object, final CMakeToolchain toolchain, final String buildConfig);
  }

  private interface Acceptor<R extends CMakeResolvedBinary> {
    void accept(final R resolvedObject);
  }

}
