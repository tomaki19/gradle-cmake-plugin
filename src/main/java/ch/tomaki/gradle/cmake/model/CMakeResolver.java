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

  public CMakeResolver(final Project project, final Stream<CMakeToolchain> toolchains,
      final Map<String, CMakeFindPackage> findPackages, final CMakeResolvedBuild resolvedBuild) {
    this.project = project;
    processToolchains(toolchains, resolvedBuild);
    this.availableFindPackages = findPackages;
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

  public void processFindPackages(final Stream<CMakeFindPackage> findPackages, final CMakeResolvedBuild resolvedBuild) {
    findPackages.forEach((findPackage) -> {
      resolvedBuild.add(new CMakeResolvedFindPackage(findPackage));
    });
  }

  public void processLibraries(final Stream<CMakeLibrary> libraries, final CMakeResolvedBuild resolvedBuild) {
    process(libraries, resolvedBuild,
        (CMakeLibrary library, CMakeResolvedToolchain resolvedToolchain, String buildConfig) -> {
          return new CMakeResolvedLibrary(library, resolvedToolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedLibrary library, CMakeResolvedToolchain toolchain) -> {
          library.addLibraryDependencies(toolchain.getPrivateLibraryLinkDependencies(), availableFindPackages, project);
          resolvedBuild.addFindPackageDependencies(library.getPrivateFindPackageDependencies());
          resolvedBuild.addFindPackageDependencies(library.getPublicFindPackageDependencies());
          resolvedBuild.addProjectModuleDependencies(library.getPrivateProjectModuleDependencies());
          resolvedBuild.addProjectModuleDependencies(library.getPublicProjectModuleDependencies());
          resolvedBuild.add(library);
          resolvedBuild.add(toolchain);
        });
  }

  public void processApplications(final Stream<CMakeApplication> applications, final CMakeResolvedBuild build) {
    process(applications, build,
        (CMakeApplication application, CMakeResolvedToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedApplication(application, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedApplication application, CMakeResolvedToolchain toolchain) -> {
          application.addLibraryDependencies(toolchain.getPrivateApplicationLinkDependencies(), availableFindPackages,
              project);
          build.addFindPackageDependencies(application.getPrivateFindPackageDependencies());
          build.addProjectModuleDependencies(application.getPrivateProjectModuleDependencies());
          build.add(application);
          build.add(toolchain);
        });
  }

  public void processTests(final Stream<CMakeTest> tests, final CMakeResolvedBuild build) {
    process(tests, build,
        (CMakeTest test, CMakeResolvedToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedTest(test, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedTest test, CMakeResolvedToolchain toolchain) -> {
          test.addLibraryDependencies(toolchain.getPrivateTestLinkDependencies(), availableFindPackages, project);
          build.addFindPackageDependencies(test.getPrivateFindPackageDependencies());
          build.addProjectModuleDependencies(test.getPrivateProjectModuleDependencies());
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
