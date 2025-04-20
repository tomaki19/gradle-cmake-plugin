/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
  private final Map<String, CMakeResolvedToolchain> availabletoolchains = new HashMap<>();
  private final Map<String, CMakeFindPackage> availableFindPackages;

  public CMakeResolver(final CMakeResolvedBuild build, final Project project,
      final Map<String, CMakeFindPackage> findPackages, final Stream<CMakeToolchain> toolchains,
      final Stream<CMakeLibrary> libraries, final Stream<CMakeApplication> applications,
      final Stream<CMakeTest> tests) {
    this.project = project;
    this.availableFindPackages = findPackages;
    processToolchains(toolchains, build);
    processLibraries(libraries, build);
    processApplications(applications, build);
    processTests(tests, build);
  }

  public void forAvailableToolchain(final String name, final Consumer<CMakeResolvedToolchain> action) {
    Optional.ofNullable(availabletoolchains.get(name)).ifPresent(action);
  }

  private void processToolchains(final Stream<CMakeToolchain> toolchains, final CMakeResolvedBuild resolvedBuild) {
    toolchains.filter((toolchain) -> Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .forEach((toolchain) -> {
          final CMakeResolvedToolchain resolvedToolchain = new CMakeResolvedToolchain(toolchain);
          availabletoolchains.put(resolvedToolchain.getName(), resolvedToolchain);
        });
  }

  private void processLibraries(final Stream<CMakeLibrary> libraries, final CMakeResolvedBuild build) {
    process(libraries, build,
        (CMakeLibrary library, CMakeResolvedToolchain resolvedToolchain, String buildConfig) -> {
          return new CMakeResolvedLibrary(library, resolvedToolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedLibrary library, CMakeResolvedToolchain toolchain) -> {
          library.addPrivateLinkDependencies(toolchain.getPrivateLibraryLinkDependencies(), availableFindPackages,
              project);
          build.addFindPackages(library.getPrivateFindPackages());
          build.addFindPackages(library.getPublicFindPackages());
          build.addProjectModules(library.getPrivateProjectModules());
          build.addProjectModules(library.getPublicProjectModules());
          build.add(library);
          build.add(toolchain);
        });
  }

  private void processApplications(final Stream<CMakeApplication> applications, final CMakeResolvedBuild build) {
    process(applications, build,
        (CMakeApplication application, CMakeResolvedToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedApplication(application, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedApplication application, CMakeResolvedToolchain toolchain) -> {
          application.addPrivateLinkDependencies(toolchain.getPrivateApplicationLinkDependencies(),
              availableFindPackages,
              project);
          build.addFindPackages(application.getPrivateFindPackages());
          build.addProjectModules(application.getPrivateProjectModules());
          build.add(application);
          build.add(toolchain);
        });
  }

  private void processTests(final Stream<CMakeTest> tests, final CMakeResolvedBuild build) {
    process(tests, build,
        (CMakeTest test, CMakeResolvedToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedTest(test, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedTest test, CMakeResolvedToolchain toolchain) -> {
          test.addPrivateLinkDependencies(toolchain.getPrivateTestLinkDependencies(), availableFindPackages,
              project);
          build.addFindPackages(test.getPrivateFindPackages());
          build.addProjectModules(test.getPrivateProjectModules());
          build.add(test);
          build.add(toolchain);
        });
  }

  private <O extends CMakeObject, R extends CMakeResolvedBinary> void process(final Stream<O> cmakeObjects,
      final CMakeResolvedBuild resolvedBuild, final ResolverWithToolchain<O, R> resolverWithToolchain,
      final AcceptorWithToolchain<R> acceptorWithToolchain) {
    cmakeObjects.forEach((cmakeObject) -> {
      if (cmakeObject.getBuildToolchains().get().isEmpty()) {
        resolvedBuild.add(new CMakeResolvedInterface(cmakeObject));
      } else {
        cmakeObject.getBuildToolchains().get().forEach((toolchainName) -> {
          Optional.ofNullable(availabletoolchains.get(toolchainName)).ifPresent((toolchain) -> {
            toolchain.getBuildConfigs().forEach((buildConfig) -> {
              final R resolvedBinary = resolverWithToolchain.resolve(cmakeObject, toolchain, buildConfig);
              acceptorWithToolchain.accept(resolvedBinary, toolchain);
            });
          });
        });
      }
    });
  }

  private interface ResolverWithToolchain<O extends CMakeObject, R extends CMakeResolvedBinary> {
    R resolve(final O cmakeObject, final CMakeResolvedToolchain resolvedToolchain, final String buildConfig);
  }

  private interface AcceptorWithToolchain<R extends CMakeResolvedBinary> {
    void accept(final R cmakeResolvedObject, final CMakeResolvedToolchain resolvedToolchain);
  }

}
