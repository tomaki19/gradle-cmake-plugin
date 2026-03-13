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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeLibraryDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

public class CMakeBinaryLibraryResolverTest {

  @Test
  void resolveNoToolchainTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
    TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PUBLIC, "-loption")),
        Arrays.asList(
            new CMakeLibraryDependencies("target").from("Package0").build(CMakeBuildType.STATIC),
            new CMakeLibraryDependencies("BinaryLibrary0").from(project.getName()).link(CMakeLinkType.SHARED)
                .build(CMakeBuildType.STATIC)));
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension);

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);
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
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(2, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(0, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(2, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveInterfaceDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(2, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
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
    TestCMakeToolchain.registerWithLibraryDependencies("Toolchain0", extension,
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PUBLIC, "-loption")),
        Arrays.asList(
            new CMakeLibraryDependencies("target").from("Package0").build(CMakeBuildType.SHARED),
            new CMakeLibraryDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE)
                .build(CMakeBuildType.SHARED),
            new CMakeLibraryDependencies("BinaryLibrary0").link(CMakeLinkType.SHARED).build(CMakeBuildType.SHARED)));
    TestCMakeBinaryLibrary.register("BinaryLibrary1", extension,
        Arrays.asList("Toolchain0"));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(2, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolvePrivateLinkDependencyTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.registerWithDependencies("BinaryLibrary1", extension,
        Arrays.asList("Toolchain0"),
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PUBLIC, "-loption")),
        Arrays.asList(
            new CMakeLibraryDependencies("target").from("Package0"),
            new CMakeLibraryDependencies("InterfaceLibrary0").link(CMakeLinkType.INTERFACE),
            new CMakeLibraryDependencies("BinaryLibrary0").link(CMakeLinkType.SHARED)));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(2, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolvePublicLinkDependencyTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.register("Toolchain0", extension);
    TestCMakeBinaryLibrary.registerWithDependencies("BinaryLibrary1", extension,
        Arrays.asList("Toolchain0"),
        Arrays.asList(new CMakeBuildItems(CMakeVisibilityType.PUBLIC, "-loption")),
        Arrays.asList(
            new CMakeLibraryDependencies("target").from("Package0"),
            new CMakeLibraryDependencies("InterfaceLibrary0").from(project.getName()).link(CMakeLinkType.INTERFACE),
            new CMakeLibraryDependencies("BinaryLibrary0").link(CMakeLinkType.SHARED)));

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(3, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);

    assertEquals("Toolchain0", toolchains[0].getName());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(2, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

}
