/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer <tkone@gmx.ch>
 *
 * SPDX-License-Identifier: MIT
 */
package ch.tomaki.gradle.cmake;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import ch.tomaki.gradle.cmake.extension.CMakeExtension;
import ch.tomaki.gradle.cmake.helper.TestCMakeApplication;
import ch.tomaki.gradle.cmake.helper.TestCMakeBinaryLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeFindPackage;
import ch.tomaki.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeApplicationResolverTest {

  @Test
  void resolveNoToolchainTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        "FindPackage0",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeApplication.register("Application0", extension);

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(0, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(0, resolvedBuild.getResolvedApplications().size());
  }

  @Test
  void resolveNoDependenciesTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        "FindPackage0",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeApplication.register("Application0", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedApplications().size());
  }

  @Test
  void resolveToolchainDependencyTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        "FindPackage0",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));

    TestCMakeApplication.register("Application0", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedApplications().size());
  }

  @Test
  void resolveLinkDependenciesTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeApplication.registerWithPrivateDependencies("Application0", extension, "Toolchain0",
        "FindPackage0",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedApplications().size());
  }
}
