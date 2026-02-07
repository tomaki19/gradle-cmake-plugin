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
import io.github.tomaki19.gradle.cmake.extension.api.CMakeCustomTaskProto;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeBinaryLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeDependencies;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeInterfaceLibrary;
import io.github.tomaki19.gradle.cmake.helper.TestCMakePackage;
import io.github.tomaki19.gradle.cmake.helper.TestCMakeToolchain;

public class CMakeToolchainResolverTest {

  @Test
  void resolveNoLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
    TestCMakeToolchain.register("Toolchain0", extension);

    assertEquals(1, extension.getPackages().size());
    assertEquals(1, extension.getToolchains().size());
    assertEquals(1, extension.getLibraries().size());
    assertEquals(0, extension.getApplications().size());
    assertEquals(0, extension.getTests().size());

    final CMakeResolver resolver = new CMakeResolver(project, extension.getPackages(), extension.getToolchains());
    final Collection<CMakeResolvedToolchain> results = resolver.process(extension.getLibraries(),
        extension.getApplications(), extension.getTests());

    final CMakeResolvedToolchain[] toolchains = results.toArray(new CMakeResolvedToolchain[results.size()]);
    assertEquals(1, toolchains.length);
  }

  @Test
  void resolveInterfaceLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension);
    TestCMakeToolchain.register("Toolchain0", extension);

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
    assertEquals(0, toolchains[0].getPackages().size());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveLibraryLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithSharedLibraryDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").from(project.getName()).getLinkShared()),
        Arrays.asList("-loption"));

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
    assertEquals(1, toolchains[0].getPackages().size());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveApplicationLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithApplicationDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").from(project.getName()).getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").getLinkShared()),
        Arrays.asList("-loption"));

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
    assertEquals(0, toolchains[0].getPackages().size());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }

  @Test
  void resolveTestLinkDependenciesTest() throws Exception {
    final Project project = ProjectBuilder.builder().build();
    final Map<CMakeCustomTaskProto, Action<CMakeCustomTaskProto>> customTasks = new HashMap<>();
    final CMakeExtension extension = project.getExtensions().create(CMakeExtension.NAME,
        CMakeExtension.class, customTasks);

    TestCMakePackage.register("Package0", extension);
    TestCMakeInterfaceLibrary.register("InterfaceLibrary0", extension);
    TestCMakeBinaryLibrary.register("BinaryLibrary0", extension,
        Arrays.asList("Toolchain0"));
    TestCMakeToolchain.registerWithTestDependencies("Toolchain0", extension,
        Arrays.asList(
            TestCMakeDependencies.create("target").from("Package0"),
            TestCMakeDependencies.create("InterfaceLibrary0").from(project.getName()).getLinkInterface(),
            TestCMakeDependencies.create("BinaryLibrary0").getLinkShared()),
        Arrays.asList("-loption"));

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
    assertEquals(0, toolchains[0].getPackages().size());
    assertEquals(1, toolchains[0].getInterfaceLibraries().size());
    assertEquals(0, toolchains[0].getStaticLibraries().size());
    assertEquals(1, toolchains[0].getSharedLibraries().size());
    assertEquals(0, toolchains[0].getApplications().size());
    assertEquals(0, toolchains[0].getTests().size());
  }
}
