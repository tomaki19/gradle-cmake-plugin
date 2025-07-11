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
import ch.tomaki.gradle.cmake.files.CMakeLinkType;

public final class CMakeResolver {

  private final Project project;
  private final Map<String, CMakeFindPackage> availablePackages;
  private final Map<String, CMakeToolchain> availableToolchains;

  public CMakeResolver(final Project project, final NamedDomainObjectContainer<CMakeFindPackage> findPackages,
      final NamedDomainObjectContainer<CMakeToolchain> toolchains) {
    this.project = project;
    this.availablePackages = findPackages.parallelStream()
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
      processObjects(object,
          (CMakeLibrary library, CMakeToolchain toolchain) -> {
            return new CMakeResolvedLibrary(library, toolchain, availablePackages, project);
          },
          (CMakeResolvedLibrary library) -> {
            build.addPackages(library.getPrivatePackages());
            build.addPackages(library.getPublicPackages());
            build.addProjects(library.getPrivateProjects());
            build.addProjects(library.getPublicProjects());
            build.add(library);
          });
    });
  }

  private void processApplications(final CMakeResolvedBuild build, final Set<CMakeApplication> applications) {
    applications.forEach((object) -> processObjects(object,
        (CMakeApplication application, CMakeToolchain toolchain) -> {
          return new CMakeResolvedApplication(application, toolchain, availablePackages, project);
        },
        (CMakeResolvedApplication application) -> {
          build.addPackages(application.getPrivatePackages());
          build.addProjects(application.getPrivateProjects());
          build.add(application);
        }));
  }

  private void processTests(final CMakeResolvedBuild build, final Set<CMakeTest> tests) {
    tests.forEach((object) -> processObjects(object,
        (CMakeTest test, CMakeToolchain toolchain) -> {
          return new CMakeResolvedTest(test, toolchain, availablePackages, project);
        },
        (CMakeResolvedTest test) -> {
          build.addPackages(test.getPrivatePackages());
          build.addProjects(test.getPrivateProjects());
          build.add(test);
        }));
  }

  private <I extends CMakeBinary, O extends CMakeResolvedBinary> void processObjects(final I object,
      final Resolver<I, O> resolver, final Acceptor<O> acceptor) {
    availableToolchains.forEach((toolchainName, toolchain) -> {
      if (object.getToolchains().get().contains(toolchainName)
          || (object instanceof CMakeLibrary && object.getToolchains().get().isEmpty())) {
        final O resolvedObject = resolver.resolve(object, toolchain);
        acceptor.accept(resolvedObject);
      }
    });
  }

  private interface Resolver<I extends CMakeBinary, O extends CMakeResolvedBinary> {
    O resolve(I binary, CMakeToolchain toolchain);
  }

  private interface Acceptor<I extends CMakeResolvedBinary> {
    void accept(I resolvedObject);
  }

  static void resolveLinkOptions(final Set<String> dependencies, final Set<String> linkOptions)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          linkOptions.add(dependency);
        } else {
          throw new IllegalArgumentException(
              "Invalid link option declaration: '%s'!".formatted(dependency));
        }
      }
    }
  }

  static void resolvePackageDependencies(final Set<String> dependencies, final Set<CMakeResolvedPackage> packages,
      final Set<CMakeResolvedPackageDependency> packageDependencies, final CMakeResolvedToolchain toolchain,
      final Map<String, CMakeFindPackage> availableFindPackages)
      throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length <= 2) {
          if (availableFindPackages.containsKey(dependencyTokens[0])) {
            final CMakeFindPackage findPackage = availableFindPackages.get(dependencyTokens[0]);
            packages.add(new CMakeResolvedPackage(findPackage, toolchain));
            packageDependencies.add(new CMakeResolvedPackageDependency(dependency));
          } else {
            throw new IllegalArgumentException("Missing find package '%s'!".formatted(dependency));
          }
        }
      }
    }
  }

  static void resolveProjectDependencies(final Set<String> dependencies, final Set<CMakeResolvedProject> projects,
      final Set<CMakeResolvedProjectDependency> projectDependencies, final CMakeResolvedToolchain toolchain,
      final Project project) throws IllegalArgumentException {
    for (final String dependency : dependencies) {
      if (!dependency.startsWith("-")) {
        final String[] dependencyTokens = dependency.split("::");
        if (dependencyTokens.length == 3) {
          final Project dependencyProject = Objects.equals(dependencyTokens[0], project.getName()) ? project
              : project.findProject(":%s".formatted(dependencyTokens[0]));
          if (Objects.nonNull(dependencyProject)) {
            if (!Objects.equals(project, dependencyProject)) {
              projects.add(new CMakeResolvedProject(dependencyProject, toolchain));
            }
            final CMakeLinkType type = CMakeLinkType.valueOf(dependencyTokens[2].toUpperCase());
            for (final String buildConfig : toolchain.getBuildConfigs()) {
              final CMakeResolvedProjectDependency resolvedProjectModule = new CMakeResolvedProjectDependency(
                  dependencyProject, dependencyTokens[1], toolchain, type, buildConfig);
              projectDependencies.add(resolvedProjectModule);
            }
          } else {
            throw new IllegalArgumentException("Missing local project '%s'!".formatted(dependencyTokens[0]));
          }
        } else if (dependencyTokens.length > 3) {
          throw new IllegalArgumentException("Invalid dependency '%s'!".formatted(dependency));
        }
      }
    }
  }

}
