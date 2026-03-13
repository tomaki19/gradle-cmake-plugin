/*
 * SPDX-FileCopyrightText: 2025 Thomas Killer
 * SPDX-License-Identifier: MIT
 */
package io.github.tomaki19.gradle.cmake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.tomaki19.gradle.cmake.extension.CMakeExtension;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeBuildItems;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.extension.api.CMakeExecutableDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeApplication;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

public class CMakeApplicationResolverTest {

  @Test
  void resolveNoToolchainTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PRIVATE, "-loption")),
        Arrays.asList(
            new CMakeExecutableDependencies("target").from("Package0"),
            new CMakeExecutableDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE),
            new CMakeExecutableDependencies("BinaryLibrary0").from(project.getName()).link(CMakeLinkType.SHARED)));
    TestCMakeApplication.register("Application0", extension);

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
        extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveMultipleToolchainTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakePackage.register("Package1", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary1", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PUBLIC, "-loption")),
        Arrays.asList(
            new CMakeExecutableDependencies("target").from("Package0").visibility(CMakeVisibilityType.PUBLIC),
            new CMakeExecutableDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE)
                .visibility(CMakeVisibilityType.PUBLIC),
            new CMakeExecutableDependencies("BinaryLibrary0")
                .from(project.getName()).link(CMakeLinkType.SHARED).visibility(CMakeVisibilityType.PUBLIC)));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain1", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PRIVATE, "-loption")),
        Arrays.asList(
            new CMakeExecutableDependencies("target").from("Package1"),
            new CMakeExecutableDependencies("InterfaceLibrary1").from(project.getName())
                .link(CMakeLinkType.INTERFACE)));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0", "Toolchain1"));

    assertEquals(2, extension.getPackages().size());
    assertEquals(2, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
        extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(2, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(2, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPublicPackageDependencies().size());
      assertEquals(2, applications[0].getPublicProjectDependencies().size());
      assertEquals(1, applications[0].getPublicLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());

    assertEquals("Toolchain1", toolchains[1].getName());
    assertEquals(2, toolchains[1].getInterfaceLibraries().size());
    assertEquals(0, toolchains[1].getStaticLibraries().size());
    assertEquals(0, toolchains[1].getSharedLibraries().size());
    assertEquals(1, toolchains[1].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[1].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[1].getApplications().size()]);
      assertEquals(1, applications[0].getPrivatePackageDependencies().size());
      assertEquals(1, applications[0].getPrivateProjectDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[1].getTests().size());
  }

  @Test
  void resolveNoDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(), Arrays.asList());
    TestCMakeToolchain.registerWithTestDependencies("Toolchain1", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PRIVATE, "-loption")),
        Arrays.asList(new CMakeExecutableDependencies("target").from("Package0")));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(2, extension.getToolchains().size());
    assertEquals(1, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
        extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(0, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(0, applications[0].getPrivatePackageDependencies().size());
      assertEquals(0, applications[0].getPrivateProjectDependencies().size());
      assertEquals(0, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveToolchainDependencyTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PRIVATE, "-loption")),
        Arrays.asList(
            new CMakeExecutableDependencies("target").from("Package0"),
            new CMakeExecutableDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE),
            new CMakeExecutableDependencies("BinaryLibrary0").from(project.getName()).link(CMakeLinkType.SHARED)));
    TestCMakeApplication.register("Application0", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
        extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPrivatePackageDependencies().size());
      assertEquals(2, applications[0].getPrivateProjectDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeApplication.registerWithDependencies("Application0", extension,
        Arrays.asList("Toolchain0"),
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PRIVATE, "-loption")),
        Arrays.asList(
            new CMakeExecutableDependencies("target").from("Package0"),
            new CMakeExecutableDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE),
            new CMakeExecutableDependencies("BinaryLibrary0").from(project.getName()).link(CMakeLinkType.SHARED)));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(1, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(),
        extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(1, toolchains[0].getApplications().size());
    {
      final CMakeResolvedExecutable[] applications = toolchains[0].getApplications()
          .toArray(new CMakeResolvedExecutable[toolchains[0].getApplications().size()]);
      assertEquals(1, applications[0].getPrivatePackageDependencies().size());
      assertEquals(2, applications[0].getPrivateProjectDependencies().size());
      assertEquals(1, applications[0].getPrivateLinkOptions().size());
    }
    assertEquals(0, toolchains[0].getTests().size());
  }
}
