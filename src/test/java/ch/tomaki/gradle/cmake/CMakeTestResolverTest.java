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
import ch.tomaki.gradle.cmake.helper.TestCMakeBinaryLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeFindPackage;
import ch.tomaki.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import ch.tomaki.gradle.cmake.helper.TestCMakeTest;
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeTestResolverTest {

  @Test
  void resolveNoToolchainTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeTest.register("Test0", extension);

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(0, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(0, resolvedBuild.getResolvedTests().size());
  }

  @Test
  void resolveNoDependenciesTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension);
    TestCMakeTest.register("Test0", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(0, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedTests().size());
    resolvedBuild.getResolvedTests().forEach((test) -> {
      assertEquals(0, test.getPrivateFindPackageDependencies().size());
      assertEquals(0, test.getPrivateProjectModuleDependencies().size());
      assertEquals(0, test.getPrivateLinkOptions().size());
    });
  }

  @Test
  void resolveToolchainDependencyTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeTest.register("Test0", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedTests().size());
    resolvedBuild.getResolvedTests().forEach((test) -> {
      assertEquals(1, test.getPrivateFindPackageDependencies().size());
      assertEquals(2, test.getPrivateProjectModuleDependencies().size());
      assertEquals(1, test.getPrivateLinkOptions().size());
    });
  }

  @Test
  void resolveLinkDependenciesTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeTest.registerWithPrivateDependencies("Test0", extension, "Toolchain0",
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(2, resolvedBuild.getResolvedLibraries().size());
    assertEquals(2, resolvedBuild.getResolvedTests().size());
    resolvedBuild.getResolvedTests().forEach((test) -> {
      assertEquals(1, test.getPrivateFindPackageDependencies().size());
      assertEquals(2, test.getPrivateProjectModuleDependencies().size());
      assertEquals(1, test.getPrivateLinkOptions().size());
    });
  }
}
