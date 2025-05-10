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
import ch.tomaki.gradle.cmake.helper.TestCMakeToolchain;
import ch.tomaki.gradle.cmake.model.CMakeResolvedBuild;
import ch.tomaki.gradle.cmake.model.CMakeResolver;

public class CMakeBinaryLibraryResolverTest {

  @Test
  void resolveNoToolchainTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
    TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension);

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(0, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(3, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(0, resolvedBuild.getResolvedLibraries().size());
  }

  @Test
  void resolveNoDependenciesTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(0, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(4, resolvedBuild.getResolvedLibraries().size());
    resolvedBuild.getResolvedLibraries().forEach((library) -> {
      assertEquals(0, library.getPrivateFindPackageDependencies().size());
      assertEquals(0, library.getPrivateProjectModuleDependencies().size());
      assertEquals(0, library.getPrivateLinkOptions().size());
      assertEquals(0, library.getPublicFindPackageDependencies().size());
      assertEquals(0, library.getPublicProjectModuleDependencies().size());
      assertEquals(0, library.getPublicLinkOptions().size());
    });
  }

  @Test
  void resolveToolchainDependencyTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension, "Toolchain0");

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(4, resolvedBuild.getResolvedLibraries().size());
    resolvedBuild.getResolvedLibraries().forEach((library) -> {
      assertEquals(1, library.getPrivateFindPackageDependencies().size());
      assertEquals(2, library.getPrivateProjectModuleDependencies().size());
      assertEquals(1, library.getPrivateLinkOptions().size());
      assertEquals(0, library.getPublicFindPackageDependencies().size());
      assertEquals(0, library.getPublicProjectModuleDependencies().size());
      assertEquals(0, library.getPublicLinkOptions().size());
    });
  }

  @Test
  void resolvePrivateLinkDependencyTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.registerWithPrivateDependencies("BinaryLibrary1", extension, "Toolchain0",
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(4, resolvedBuild.getResolvedLibraries().size());
    resolvedBuild.getResolvedLibraries().forEach((library) -> {
      if ("BinaryLibrary0".equals(library.getName())) {
        assertEquals(0, library.getPrivateFindPackageDependencies().size());
        assertEquals(0, library.getPrivateProjectModuleDependencies().size());
        assertEquals(0, library.getPrivateLinkOptions().size());
        assertEquals(0, library.getPublicFindPackageDependencies().size());
        assertEquals(0, library.getPublicProjectModuleDependencies().size());
        assertEquals(0, library.getPublicLinkOptions().size());
      } else {
        assertEquals(1, library.getPrivateFindPackageDependencies().size());
        assertEquals(2, library.getPrivateProjectModuleDependencies().size());
        assertEquals(1, library.getPrivateLinkOptions().size());
        assertEquals(0, library.getPublicFindPackageDependencies().size());
        assertEquals(0, library.getPublicProjectModuleDependencies().size());
        assertEquals(0, library.getPublicLinkOptions().size());
      }
    });
  }

  @Test
  void resolvePublicLinkDependencyTest() {
    final Project project = ProjectBuilder.builder().build();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME, CMakeExtension.class);

    TestCMakeFindPackage.register("FindPackage0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension, "Toolchain0");
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.registerWithPublicDependencies("BinaryLibrary1", extension, "Toolchain0",
        "FindPackage0", "-loption",
        "%s::InterfaceLibrary0::interface".formatted(project.getName()),
        "%s::BinaryLibrary0::shared".formatted(project.getName()));

    final CMakeResolver cmakeResolver = new CMakeResolver(project, extension.getFindPackages(),
        extension.getToolchains());
    final CMakeResolvedBuild resolvedBuild = cmakeResolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    assertEquals(1, resolvedBuild.getResolvedFindPackages().size());
    assertEquals(1, resolvedBuild.getResolvedInterfaces().size());
    assertEquals(4, resolvedBuild.getResolvedLibraries().size());
    resolvedBuild.getResolvedLibraries().forEach((library) -> {
      if ("BinaryLibrary0".equals(library.getName())) {
        assertEquals(0, library.getPrivateFindPackageDependencies().size());
        assertEquals(0, library.getPrivateProjectModuleDependencies().size());
        assertEquals(0, library.getPrivateLinkOptions().size());
        assertEquals(0, library.getPublicFindPackages().size());
        assertEquals(0, library.getPublicProjectModuleDependencies().size());
        assertEquals(0, library.getPublicLinkOptions().size());
      } else {
        assertEquals(0, library.getPrivateFindPackageDependencies().size());
        assertEquals(0, library.getPrivateProjectModuleDependencies().size());
        assertEquals(0, library.getPrivateLinkOptions().size());
        assertEquals(1, library.getPublicFindPackageDependencies().size());
        assertEquals(2, library.getPublicProjectModuleDependencies().size());
        assertEquals(1, library.getPublicLinkOptions().size());
      }
    });
  }

}
