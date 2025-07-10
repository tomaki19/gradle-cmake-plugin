/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.gradle.api.NamedDomainObjectContainer;
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

  public CMakeResolver(final Project project, final NamedDomainObjectContainer<CMakeFindPackage> findPackages,
      final NamedDomainObjectContainer<CMakeToolchain> toolchains) {
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
      if (!object.getSources().isPresent() || object.getSources().get().isEmpty()) {
        availableToolchains.forEach((toolchainName, toolchain) -> {
          final CMakeResolvedInterfaceLibrary library = new CMakeResolvedInterfaceLibrary(object, toolchain,
              availableFindPackages, project);
          build.add(library);
        });
      } else {
        processObjects(object,
            (CMakeLibrary library, CMakeToolchain toolchain) -> {
              return new CMakeResolvedBinaryLibrary(library, toolchain, availableFindPackages, project);
            },
            (CMakeResolvedBinaryLibrary library) -> {
              build.addFindPackages(library.getPrivatePackages());
              build.addFindPackages(library.getPublicPackages());
              build.addProjectModules(library.getPrivateProjects());
              build.addProjectModules(library.getPublicProjects());
              build.add(library);
            });
      }
    });
  }

  private void processApplications(final CMakeResolvedBuild build, final Set<CMakeApplication> applications) {
    applications.forEach((object) -> processObjects(object,
        (CMakeApplication application, CMakeToolchain toolchain) -> {
          return new CMakeResolvedApplication(application, toolchain, availableFindPackages, project);
        },
        (CMakeResolvedApplication application) -> {
          build.addFindPackages(application.getPrivatePackages());
          build.addProjectModules(application.getPrivateProjects());
          build.add(application);
        }));
  }

  private void processTests(final CMakeResolvedBuild build, final Set<CMakeTest> tests) {
    tests.forEach((object) -> processObjects(object,
        (CMakeTest test, CMakeToolchain toolchain) -> {
          return new CMakeResolvedTest(test, toolchain, availableFindPackages, project);
        },
        (CMakeResolvedTest test) -> {
          build.addFindPackages(test.getPrivatePackages());
          build.addProjectModules(test.getPrivateProjects());
          build.add(test);
        }));
  }

  private <U extends CMakeBinary, R extends CMakeResolvedBinary> void processObjects(final U binary,
      final Resolver<U, R> resolver, final Acceptor<R> acceptor) {
    binary.getToolchains().get().forEach((toolchainName) -> {
      Optional.ofNullable(availableToolchains.get(toolchainName)).ifPresent((toolchain) -> {
        final R resolvedBinary = resolver.resolve(binary, toolchain);
        acceptor.accept(resolvedBinary);
      });
    });
  }

  private interface Resolver<U extends CMakeBinary, R extends CMakeResolvedBinary> {
    R resolve(U binary, CMakeToolchain toolchain);
  }

  private interface Acceptor<R extends CMakeResolvedBinary> {
    void accept(R resolvedObject);
  }

}
