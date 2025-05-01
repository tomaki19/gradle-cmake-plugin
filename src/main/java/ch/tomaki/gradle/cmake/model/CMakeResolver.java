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
  private final Map<String, CMakeToolchain> availabletoolchains = new HashMap<>();
  private final Map<String, CMakeFindPackage> availableFindPackages;

  public CMakeResolver(final Project project, final Map<String, CMakeFindPackage> findPackages) {
    this.project = project;
    this.availableFindPackages = findPackages;
  }

  public void process(final CMakeResolvedBuild build, final Stream<CMakeToolchain> toolchains,
      final Stream<CMakeLibrary> libraries, final Stream<CMakeApplication> applications,
      final Stream<CMakeTest> tests) {
    processToolchains(toolchains, build);
    processLibraries(libraries, build);
    processApplications(applications, build);
    processTests(tests, build);
  }

  private void processToolchains(final Stream<CMakeToolchain> toolchains, final CMakeResolvedBuild resolvedBuild) {
    toolchains.filter((toolchain) -> Objects.equals(OperatingSystem.current(), toolchain.getOperatingSystem().get()))
        .forEach((toolchain) -> availabletoolchains.put(toolchain.getName(), toolchain));
  }

  private void processLibraries(final Stream<CMakeLibrary> libraries, final CMakeResolvedBuild build) {
    process(libraries, build,
        (CMakeLibrary library, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedLibrary(library, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedLibrary library) -> {
          build.addFindPackages(library.getPrivateFindPackages());
          build.addFindPackages(library.getPublicFindPackages());
          build.addProjectModules(library.getPrivateProjectModules());
          build.addProjectModules(library.getPublicProjectModules());
          build.add(library);
          build.add(library.getResolvedToolchain());
        });
  }

  private void processApplications(final Stream<CMakeApplication> applications, final CMakeResolvedBuild build) {
    process(applications, build,
        (CMakeApplication application, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedApplication(application, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedApplication application) -> {
          build.addFindPackages(application.getPrivateFindPackages());
          build.addProjectModules(application.getPrivateProjectModules());
          build.add(application);
          build.add(application.getResolvedToolchain());
        });
  }

  private void processTests(final Stream<CMakeTest> tests, final CMakeResolvedBuild build) {
    process(tests, build,
        (CMakeTest test, CMakeToolchain toolchain, String buildConfig) -> {
          return new CMakeResolvedTest(test, toolchain, buildConfig, availableFindPackages, project);
        },
        (CMakeResolvedTest test) -> {
          build.addFindPackages(test.getPrivateFindPackages());
          build.addProjectModules(test.getPrivateProjectModules());
          build.add(test);
          build.add(test.getResolvedToolchain());
        });
  }

  private <O extends CMakeObject, R extends CMakeResolvedBinary> void process(final Stream<O> cmakeObjects,
      final CMakeResolvedBuild resolvedBuild, final Resolver<O, R> resolver, final Acceptor<R> acceptor) {
    cmakeObjects.forEach((cmakeObject) -> {
      if (cmakeObject.getToolchains().get().isEmpty()) {
        resolvedBuild.add(new CMakeResolvedInterface(cmakeObject));
      } else {
        cmakeObject.getToolchains().get().forEach((toolchainName) -> {
          Optional.ofNullable(availabletoolchains.get(toolchainName)).ifPresent((toolchain) -> {
            toolchain.getBuildConfigs().get().forEach((buildConfig) -> {
              final R resolvedBinary = resolver.resolve(cmakeObject, toolchain, buildConfig);
              acceptor.accept(resolvedBinary);
            });
          });
        });
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
