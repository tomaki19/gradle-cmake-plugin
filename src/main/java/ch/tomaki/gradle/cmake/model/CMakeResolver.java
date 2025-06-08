/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.internal.os.OperatingSystem;

import ch.tomaki.gradle.cmake.extension.api.CMakeApplication;
import ch.tomaki.gradle.cmake.extension.api.CMakeBinary;
import ch.tomaki.gradle.cmake.extension.api.CMakeFindPackage;
import ch.tomaki.gradle.cmake.extension.api.CMakeLibrary;
import ch.tomaki.gradle.cmake.extension.api.CMakeTest;
import ch.tomaki.gradle.cmake.extension.api.CMakeToolchain;

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

  public void forToolchains(final Consumer<CMakeToolchain> action) {
    availableToolchains.forEach((name, toolchain) -> action.accept(toolchain));
  }

  public void forToolchain(final String name, final Consumer<CMakeToolchain> action) {
    Optional.ofNullable(availableToolchains.get(name)).ifPresent(action);
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
    libraries.forEach((object) -> {
      if (object.getToolchains().get().isEmpty()) {
        build.add(new CMakeResolvedInterfaceLibrary(object, availableFindPackages, project));
      } else {
        processObjects(object,
            (CMakeLibrary library, CMakeToolchain toolchain, String buildConfig) -> {
              return new CMakeResolvedBinaryLibrary(library, toolchain, buildConfig, availableFindPackages, project);
            },
            (CMakeResolvedBinaryLibrary library) -> {
              build.addFindPackages(library.getPrivateFindPackages());
              build.addFindPackages(library.getPublicFindPackages());
              build.addProjectModules(library.getPrivateProjectModules());
              build.addProjectModules(library.getPublicProjectModules());
              build.add(library);
            });
      }
    });
  }

  private void processApplications(final CMakeResolvedBuild build, final Set<CMakeApplication> applications) {
    applications.forEach((object) -> processObjects(object,
        (CMakeApplication application, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedApplication(application, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedApplication application) -> {
          build.addFindPackages(application.getPrivateFindPackages());
          build.addProjectModules(application.getPrivateProjectModules());
          build.add(application);
        }));
  }

  private void processTests(final CMakeResolvedBuild build, final Set<CMakeTest> tests) {
    tests.forEach((object) -> processObjects(object,
        (CMakeTest test, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedTest(test, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedTest test) -> {
          build.addFindPackages(test.getPrivateFindPackages());
          build.addProjectModules(test.getPrivateProjectModules());
          build.add(test);
        }));
  }

  private <U extends CMakeBinary, R extends CMakeResolvedBinary> void processObjects(final U binary,
      final Resolver<U, R> resolver, final Acceptor<R> acceptor) {
    binary.getToolchains().get().forEach((toolchainName) -> {
      Optional.ofNullable(availableToolchains.get(toolchainName)).ifPresent((toolchain) -> {
        if (toolchain.getBuildConfigs().get().isEmpty()) {
          processBuildConfigs(CMakeToolchain.BuildConfigDefaults, toolchain, binary, resolver, acceptor);
        } else {
          processBuildConfigs(toolchain.getBuildConfigs().get(), toolchain, binary, resolver, acceptor);
        }
      });
    });
  }

  private <U extends CMakeBinary, R extends CMakeResolvedBinary> void processBuildConfigs(
      final Collection<String> buildConfigs, final CMakeToolchain toolchain, final U binary,
      final Resolver<U, R> resolver, final Acceptor<R> acceptor) {
    buildConfigs.forEach((buildConfig) -> {
      final R resolvedBinary = resolver.resolve(binary, toolchain, buildConfig);
      acceptor.accept(resolvedBinary);
    });
  }

  private interface Resolver<U extends CMakeBinary, R extends CMakeResolvedBinary> {
    R resolve(final U binary, final CMakeToolchain toolchain, final String buildConfig);
  }

  private interface Acceptor<R extends CMakeResolvedBinary> {
    void accept(final R resolvedObject);
  }

}
